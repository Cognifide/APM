/*-
 * ========================LICENSE_START=================================
 * AEM Permission Management
 * %%
 * Copyright (C) 2013 - 2016 Cognifide Limited
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */
Cog.component.cqsmImport = (function ($) {

    var api = {},
        ongoing = false,
        helper = Cog.component.cqsmHelper;

    function submitForm() {
        document.getElementById('form').submit();
    }

    function renderProgress(fileName, mode, jobId, item, start) {

        setLastExecutionTime(mode, item, start);

        $.ajax({
            type: "GET",
            url: "/etc/cqsm.ajax.html",
            data: {'fileName': fileName, 'wcmmode': 'disabled', 'mode': mode, 'progressJobId': jobId},
            dataType: "html",
            success: function(data) {
                Cog.fireEvent("loader", "showLoaderListener",  {
                    context: $("#cqsmImportPage")
                });
                ongoing = false;
                $("#import-summary").empty().append(data);
                var time = new Date().getTime() - start;
                var statusMessageType = $("#status-message-type").text();
                if (statusMessageType === "error") {
                    helper.showMessage("File: " + fileName + "<br>Mode: " + mode + "<br>Executed with errors in " + time + " ms", statusMessageType, true);
                } else {
                    helper.showMessage("File: " + fileName + "<br>Mode: " + mode + "<br>Executed successfully in " + time + " ms", statusMessageType, true);
                }
                if (item !== null) {
                    var newClass = (statusMessageType === "error" ? 'dryRunFailed' : 'dryRunSuccess');
                    item.attr('class', newClass);
                }
            }
        });
    }

    function importCqsm(fileName, mode, item) {
        var start = new Date().getTime();

        Cog.fireEvent("loader", "hideLoaderListener", {
            context: $("#cqsmImportPage")
        });

        $.ajax({
            type: "POST",
            url: "/bin/cqsm/run-background?file=" + fileName + "&mode=" + mode,
            dataType: "html",
            success: function (data) {
                var jobId = JSON.parse(data).id;

                setTimeout(function(){(function checkStatus(jobId) {
                    $.ajax({
                        type: "GET",
                        url: "/bin/cqsm/run-background?id=" + jobId,
                        dataType: "html",
                        success: function (data) {
                            var dataObject = JSON.parse(data);
                            if (dataObject.type === 'running') {
                                setTimeout(function(){checkStatus(jobId)}, 1000);
                            } else if (dataObject.type === 'unknown') {
                                var time = new Date().getTime() - start;
                                helper.showMessage("File: " + fileName + "<br>Mode: " + mode + "<br>Executed with errors in " + time + " ms", dataObject.type, true);
                                Cog.fireEvent("loader", "showLoaderListener", {
                                    context: $("#cqsmImportPage")
                                });
                            }
                        },
                        complete: function () {
                            renderProgress(fileName, mode, jobId, item, start);
                        }
                    });
                })(jobId)}, 100);
            }
        });
    }

    function setLastExecutionTime(mode, item, start) {
        if (mode === 'RUN') {
            var date = new Date(start);
            var time = date.format("M d, Y h:i:s A");
            var lastRunField = item.closest("tr").find(".last-run-author");
            lastRunField.innerText = time;
        }
    }

    function runOnPublish(fileName, item) {

        $.ajax({
            type: "GET",
            url: "/bin/cqsm/replicate?run=publish&fileName=" + fileName,
            dataType: "json",
            success: function(data) {
                $("#import-summary").empty()
                helper.showMessage(data.message, data.type, false);
            }
        });
    }

    api.init = function ($elements) {

        var errorMessage = 'Unexpected Error Happened';

        $elements.each(function(){
            helper.setMaxRows(10);
            helper.processList();

            $(document).ajaxError(function(event, jqxhr, settings, exception) {
                helper.showMessage(errorMessage, "error",false);
            });
            $(".mode-dry-run", $elements).click(function() {
                var fileName = helper.getFileName(this);
                var currentRow = $(this).closest("tr");
                var dryRunStatusDiv = currentRow.find(".dryRunStatus div");
                importCqsm(fileName, "DRY_RUN", dryRunStatusDiv);
            });
            $(".mode-import", $elements).click(function() {
                var fileName = helper.getFileName(this);
                var result = confirm("Do you really want to run this script?");
                if (result) {
                    var currentRow = $(this).closest("tr");
                    var dryRunStatusDiv = currentRow.find(".dryRunStatus div");
                    importCqsm(fileName, "RUN", dryRunStatusDiv);
                }
            });
            $(".mode-run-publish", $elements).click(function() {
                var fileName = helper.getFileName(this);
                var result = confirm("Do you really want to run this script on publish?");
                if (result) {
                    var currentRow = $(this).closest("tr");
                    var dryRunStatusDiv = currentRow.find(".dryRunStatus div");
                    runOnPublish(fileName, dryRunStatusDiv);
                }
            });

            $(".action-edit", $elements).click(function() {
                var fileName = helper.getFileName(this);
                var win = helper.openWindow("/etc/cqsm/pages/console.html?path=" + fileName, "Console", 200, 170);

                win.focus();
            });

            $(".action-remove", $elements).click(function() {
                var fileName = helper.getFileName(this);
                var fileRowId = helper.getFileRowId(this);
                var result = confirm("Do you really want to remove this script?");

                if (result) {
                    $.ajax({
                        type: "POST",
                        data: { 'file' : fileName },
                        dataType: "json",
                        url :  '/bin/cqsm/remove',
                        success: function(data) {
                            if (data == null || data.type == "error") {
                                var msg;
                                data.message == null ? msg = errorMessage : msg =  data.message;

                                helper.showMessage(msg, "error", false);
                            } else {
                                $("#" + fileRowId).remove();
                                $("#import-summary").empty();
                                helper.processList();
                                helper.showMessage("File: " + fileName + "<br>Removed successfully", "success", false);
                            }
                        }
                    });
                }
            });

            $(".execution-enabled", $elements).change(function() {
                var executionEnabled = $(this).prop('checked');
                var fileName = helper.getFileName(this);

                $.ajax({
                    type: "POST",
                    data: {
                        "executionEnabled": executionEnabled,
                        "scriptPath": fileName,
                    },
                    url: "/bin/cqsm/scriptConfig",
                    success: function() {
                        helper.showMessage("File: " + fileName + "<br>Configuration updated successfully",
                            "success", false);
                    }
                });
            });

            $("#remove-all", $elements).click(function() {
                var result = confirm("Do you really want to remove all script?");
                if (result) {
                    $.ajax({
                        type: "POST",
                        url: "/bin/cqsm/remove",
                        data: { "confirmation" : result },
                        dataType: "json",
                        success: function(data) {
                            $("#cqsm-files > tbody").empty();
                            $(".navigation").remove();
                            $("#import-summary").empty();
                            helper.showMessage(data.message, data.type, false);
                        }
                    });
                }
            });

            $("#open-console", $elements).click(function() {
                var win = helper.openWindow("/etc/cqsm/pages/console.html","Console",200,170);
                win.focus();
            });

            $(".action-view", $elements).click(function() {
                var fileName = helper.getFileName(this);
                var filePath = helper.getFilePath(this);
                var win = helper.openWindow("/etc/cqsm/pages/viewCqsmFile.html?wcmmode=disabled&filename="+ fileName
                    + "&filepath="+filePath,"CQSM file",200,170);
                win.focus();
            });

            $("#fileToUpload", $elements).change(submitForm);

            $("#uploadButton", $elements).click(function() {
                $("#fileToUpload", $elements).click();
            });

        });
    }

    return api;
}(COGjQuery));

Cog.register({
    name: 'cqsmImport',
    api: Cog.component.cqsmImport,
    selector: '#cqsmImportPage'
});

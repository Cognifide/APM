## HTTP API
Permission management and maintenance, having the well defined suie of scripts can be easily automated using the Continous Integration approach.

The automation is particulary useful for the [[check actions|CheckActions]] based scripts. This way an auto health-check for permission scheme can inform about a critical misconfiguration.

### Operations

- [script list](#how-to-get-list-of-scripts)
- [upload script](#how-to-upload-script)
- [run script](#how-to-run-script)
- [run script asynchronously](#how-to-run-script-asynchronously)
- [validate script](#how-to-validate-script)
- [remove script](#how-to-remove-script)
- [history](#how-to-get-history-of-executed-scripts)

#### How to get list of scripts?

**Request**
```bash
curl -i -u admin:admin http://localhost:4502/bin/apm/list
```
Request has no available parameters to set.

**Response**
```bash
[
  {
  "fileName": "filename.cqsm",
  "author": "admin",
  "executionEnabled": true,
  "lastExecuted": "Oct 3, 2016 8:20:52 AM",
  "lastModified": "Sep 30, 2016 10:15:24 AM",
  "valid": true,
  "path": "/etc/cqsm/import/jcr:content/cqsmImport/filename.cqsm",
  "dryRunExecuted": true,
  "dryRunSuccessful": true,
  "executionMode": "ON_DEMAND"
  },
  {
  "fileName": "filename-2.cqsm",
  "author": "admin",
  "executionEnabled": true,
  "lastExecuted": "Oct 3, 2016 8:21:04 AM",
  "lastModified": "Sep 30, 2016 10:15:06 AM",
  "valid": true,
  "path": "/etc/cqsm/import/jcr:content/cqsmImport/filename-2.cqsm",
  "dryRunExecuted": true,
  "dryRunSuccessful": false,
  "executionMode": "ON_DEMAND"
  }
]
```
Response returns every properties set on the request:
* General node properties (i.e author, fileName, lastModified, path)
* Script specific properties:
  * dryRunExecuted - (Boolean) if dry run was executed
  * dryRunSuccessful - (Boolean) if script succeeded dry run 
  * executionEnabled - (Boolean) if automated script execution is enabled
  * executionMode - (ON_DEMAND, ON_START, ON_MODIFY, ON_SCHEDULE). To get more information about automatic script execution modes read  [documentation](https://github.com/Cognifide/APM/wiki/Autorun) to get more information about 
  * lastExecuted - (Date) - last time script was executed 
  * valid - (Boolean) if script passes validation


#### How to upload script?
**Request**
```bash
curl -i -u admin:admin -H "file=permissions.cqsm" -X POST http://localhost:4502/bin/apm/fileUpload -F file=@"misc/example scripts/permissions.cqsm"
```
**Response**
```bash
[
  {
    "verified": true,
    "executionEnabled": true,
    "path": "/etc/cqsm/cqsmImport/jcr:content/cqsmImport/permissions.cqsm",
    "executionMode": "ON_DEMAND"
  }
]
```
#### How to run script?
**Request**

```bash
curl -i -u admin:admin -d "file=permissions.cqsm&mode=DRY_RUN" -X POST http://localhost:4502/bin/apm/run
```
Available modes to use:
* DRY_RUN,
* RUN,
* VALIDATION

```bash
[
  ...
  {
    "authorizable": "test_group",
    "actionName": "com.cognifide.cq.apm.foundation.actions.deny.Deny",
    "parameters": "/content/geometrixx/it /* ALL",
    "messages": [
      {
        "text": "Added deny privilege for test_group on /content/geometrixx/it",
        "type": "info"
      }
    ],
    "status": "SUCCESS"
  }
]
```

> This operation on large content may last few minutes and in that case it may result in network read timeout exception. If such error occurs please change to asynchronous model.

#### How to run script asynchronously?
To run asynchronously a user has to make `POST` request to `/bin/apm/run-background`. In the result there will be `id` of the job, which could be used later to ask for the job status.

To ask for a job status user have to make `GET` request to `/bin/apm/run-background` with `id` parameter. If the job is still running in the result the json object has type property with running value. After another requests the json object with type property finished should be returned, the result will contain also all messages for each instruction execution.

**Request**
```bash
curl -i -u admin:admin -d "file=permissions.cqsm&mode=DRY_RUN" -X POST http://localhost:4502/bin/apm/run-background
```
**Response**
```bash
{
  "id": "2016/4/22/9/42/0ea10a55-2dbb-4c1b-979c-98b9f1f7a73d_20",
  "type": "background",
  "message": "background"
}
```

**Request**
```bash
curl -i -u admin:admin -d "id=2016/4/22/9/42/0ea10a55-2dbb-4c1b-979c-98b9f1f7a73d_20" -X GET http://localhost:4502/bin/apm/run-background
```
**Response**
```bash
{
  "type": "running"
}
```

**Request**
```bash
curl -i -u admin:admin -d "id=2016/4/22/9/42/0ea10a55-2dbb-4c1b-979c-98b9f1f7a73d_20" -X GET http://localhost:4502/bin/apm/run-background
```
**Response**
```bash
{
  "type": "finished",
  "entries": [
    {
      "actionName": "Define",
      "command": "DEFINE path /content",
      "parameters": "path /content",
      "messages": [
        {
          "text": "Definition saved",
          "type": "info"
        }
      ],
      "status": "SUCCESS"
    },
    {
      "actionName": "CreateAuthorizable",
      "command": "CREATE USER author secret",
      "parameters": "author secret",
      "messages": [
        {
          "text": "Authorizable with id: author already exists, and is a User",
          "type": "error"
        }
      ],
      "status": "ERROR"
    }
  ]
}
```


> Job details are stored in memory only for 10 minutes after successful run. After that the json object with type: "unknown" will be returned.

#### How to validate script?
**Request**
```bash
curl -i -u admin:admin -d "content=CREATE USER foo" -X POST http://localhost:4502/bin/apm/validate
```
**Response**
```bash
{
  "message": "Script passes validation",
  "type": "success"
}
```

**Request**
```bash
curl -i -u admin:admin -d "content=IMPORT nonexisting.cqsm" -X POST http://localhost:4502/bin/apm/validate
```
**Response**
```bash
{
  "message": "Script does not pass validation",
  "error": "Included script: \u0027nonexisting.cqsm\u0027 does not exists.",
  "type": "error"
}
```

#### How to remove script?
**Request**
```bash
curl -i -u admin:admin -d "file=permissions.cqsm" -X POST http://localhost:4502/bin/apm/remove
```
**Response**
```bash
{
  "message": "Script removed successfully: /etc/cqsm/cqsmImport/jcr:content/cqsmImport/permissions.cqsm",
  "type": "success"
}
```

**Request**
```bash
curl -i -u admin:admin -d "confirmation=true" -X POST http://localhost:4502/bin/apm/remove
```
**Response**
```bash
{
  "message": "Scripts removed successfully, total: 1",
  "paths": [
    "/etc/cqsm/cqsmImport/jcr:content/cqsmImport/permissions.cqsm"
  ],
  "type": "success"
}
```

#### How to get history of executed scripts?
History can be filtered using optional **filter** parameter. Available filters:
* automatic run - for script executed [automatically](https://github.com/Cognifide/APM/wiki/Autorun) (automatic%20run)
* author - for script executed on author
* publish - for script executed on publish

**Request**
```bash
curl -i -u admin:admin -X GET http://localhost:4502/bin/apm/history?filter=publish
```
**Response**
```bash
[{
   "fileName": "filename.cqsm",
   "executionTime": "Sep 29, 2016 12:35:23 PM",
   "author": "admin",
   "uploadTime": "Sep 29, 2016 11:29:17 AM",
   "instanceType": "publish",
   "instanceHostname": "PUIG",
   "mode": "AUTOMATIC_RUN",
   "executor": "automatic run",
   "executionSummary": [
     {
       "messages": [
         {
           "text": "Definition saved",
           "type": "info"
         }
       ],
       "status": "SUCCESS"
     },
     {
       "messages": [
         {
           "text": "Authorizable with id: author already exists, and is a User",
           "type": "error"
         }
       ],
       "status": "ERROR"
     }
   ],
   "path": "/etc/cqsm/history/jcr:content/cqsmHistory/filename.cqsm1",
   "filePath": "/etc/cqsm/history/jcr:content/cqsmHistory/filename.cqsm1/script"
}]
```
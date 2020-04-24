/*
 * ========================LICENSE_START=================================
 * AEM Permission Management
 * %%
 * Copyright (C) 2013 Cognifide Limited
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */

package com.cognifide.cq.cqsm.core.actions;

import com.cognifide.apm.grammar.ApmInteger;
import com.cognifide.apm.grammar.ApmList;
import com.cognifide.apm.grammar.ApmString;
import com.cognifide.apm.grammar.ApmType;
import com.cognifide.apm.api.actions.Action;
import com.cognifide.apm.api.actions.annotations.Flags;
import com.cognifide.apm.api.actions.annotations.Mapper;
import com.cognifide.apm.api.actions.annotations.Mapping;
import com.cognifide.apm.api.actions.annotations.Named;
import com.cognifide.apm.api.actions.annotations.Required;
import com.cognifide.cq.cqsm.api.exceptions.InvalidActionMapperException;
import com.cognifide.cq.cqsm.core.actions.ParameterDescriptor.FlagsParameterDescriptor;
import com.cognifide.cq.cqsm.core.actions.ParameterDescriptor.NamedParameterDescriptor;
import com.cognifide.cq.cqsm.core.actions.ParameterDescriptor.RequiredParameterDescriptor;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

public class MapperDescriptorFactory {

  public MapperDescriptor create(Class<?> mapperClass) {
    Mapper mapperAnnotation = mapperClass.getDeclaredAnnotation(Mapper.class);
    if (mapperAnnotation == null) {
      throw new InvalidActionMapperException("Mapper must be annotated with " + Mapper.class.getName());
    }

    final Object mapper = createInstance(mapperClass);
    final String name = mapperAnnotation.value();
    final String group = mapperAnnotation.group();
    final List<MappingDescriptor> mappingDescriptors = Lists.newArrayList();
    for (Method method : mapperClass.getDeclaredMethods()) {
      create(mapperAnnotation, method).ifPresent(mappingDescriptors::add);
    }
    return new MapperDescriptor(mapper, name, group, ImmutableList.copyOf(mappingDescriptors));
  }

  private Object createInstance(Class<?> mapperClass) {
    try {
      return mapperClass.newInstance();
    } catch (InstantiationException e) {
      throw new InvalidActionMapperException("Cannot instantiate action mapper", e);
    } catch (IllegalAccessException e) {
      throw new InvalidActionMapperException("Cannot construct action mapper, is constructor non public?", e);
    }
  }

  private Optional<MappingDescriptor> create(Mapper mapper, Method method) {
    Mapping mapping = method.getAnnotation(Mapping.class);
    if (mapping == null) {
      return Optional.empty();
    }
    if (!Action.class.equals(method.getReturnType())) {
      throw new InvalidActionMapperException("Mapping method must have return type " + Action.class.getName());
    }

    List<ParameterDescriptor> parameterDescriptors = Lists.newArrayList();
    Type[] types = method.getGenericParameterTypes();
    Annotation[][] annotations = method.getParameterAnnotations();
    int requiredIndex = 0;
    for (int i = 0; i < types.length; i++) {
      Type type = types[i];
      Annotation[] parameterAnnotations = annotations[i];
      Class<? extends ApmType> apmType = getApmType(type);
      ParameterDescriptor parameterDescriptor = null;
      if (containsAnnotation(parameterAnnotations, Named.class)) {
        Named namedAnnotation = getAnnotation(parameterAnnotations, Named.class);
        parameterDescriptor = new NamedParameterDescriptor(apmType, namedAnnotation);
      }
      if (containsAnnotation(parameterAnnotations, Flags.class)) {
        Flags flagsAnnotation = getAnnotation(parameterAnnotations, Flags.class);
        parameterDescriptor = new FlagsParameterDescriptor(apmType, flagsAnnotation);
      }
      if (parameterDescriptor == null) {
        Required requiredAnnotation = getAnnotation(parameterAnnotations, Required.class);
        parameterDescriptor = new RequiredParameterDescriptor(apmType, requiredIndex, requiredAnnotation);
        requiredIndex++;
      }
      parameterDescriptors.add(parameterDescriptor);
    }

    return Optional.of(new MappingDescriptor(method, mapper, mapping, ImmutableList.copyOf(parameterDescriptors)));
  }

  private <T extends Annotation> T getAnnotation(Annotation[] annotations, Class<T> type) {
    for (int i = 0; i < annotations.length; i++) {
      if (type.isInstance(annotations[i])) {
        return (T) annotations[i];
      }
    }
    return null;
  }

  private <T extends Annotation> boolean containsAnnotation(Annotation[] annotations, Class<T> type) {
    return getAnnotation(annotations, type) != null;
  }

  private Class<? extends ApmType> getApmType(Type type) {
    if (type instanceof Class) {
      Class aClass = (Class) type;
      if (String.class.equals(aClass)) {
        return ApmString.class;
      }
      if (Integer.class.equals(aClass)) {
        return ApmInteger.class;
      }
    } else if (type instanceof ParameterizedType) {
      ParameterizedType parameterizedType = (ParameterizedType) type;
      Class rawType = (Class) parameterizedType.getRawType();
      if (List.class.equals(rawType)) {
        return ApmList.class;
      }
    }
    return null;
  }
}

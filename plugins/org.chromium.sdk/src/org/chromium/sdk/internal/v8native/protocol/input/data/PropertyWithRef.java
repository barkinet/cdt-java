// Copyright (c) 2009 The Chromium Authors. All rights reserved.
// This program and the accompanying materials are made available
// under the terms of the Eclipse Public License v1.0 which accompanies
// this distribution, and is available at
// http://www.eclipse.org/legal/epl-v10.html

package org.chromium.sdk.internal.v8native.protocol.input.data;

import org.chromium.sdk.internal.protocolparser.JsonOptionalField;
import org.chromium.sdk.internal.protocolparser.JsonSubtype;
import org.chromium.sdk.internal.protocolparser.JsonSubtypeCondition;
import org.chromium.sdk.internal.protocolparser.JsonType;

@JsonType
public interface PropertyWithRef extends JsonSubtype<PropertyObject> {
  @JsonSubtypeCondition(fieldIsAbsent=true)
  @JsonOptionalField
  Void value();

  long ref();

  @JsonOptionalField
  Object attributes();

  @JsonOptionalField
  Long propertyType();
}

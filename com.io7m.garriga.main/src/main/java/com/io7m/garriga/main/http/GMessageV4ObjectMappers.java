/*
 * Copyright © 2024 Mark Raynsford <code@io7m.com> https://www.io7m.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
 * SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
 * IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.io7m.garriga.main.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.io7m.dixmont.core.DmJsonRestrictedDeserializers;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

/**
 * Functions to create JSON mappers for v1 messages.
 */

public final class GMessageV4ObjectMappers
{
  private GMessageV4ObjectMappers()
  {

  }

  /**
   * @return A new mapper for v1 messages
   */

  public static ObjectMapper createMapper()
  {
    final JsonMapper mapper =
      JsonMapper.builder()
        .disable(FAIL_ON_UNKNOWN_PROPERTIES)
        .build();

    final var deserializers =
      DmJsonRestrictedDeserializers.builder()
        .allowClass(GAlertManagerRequestV4.class)
        .allowClass(GAlertV4.class)
        .allowClass(String.class)
        .allowClass(int.class)
        .allowClassName("java.util.Map<java.lang.String,java.lang.String>")
        .allowClassName("java.util.List<com.io7m.garriga.main.http.GAlertV4>")
        .build();

    final var simpleModule = new SimpleModule();
    simpleModule.setDeserializers(deserializers);
    mapper.registerModule(simpleModule);
    return mapper;
  }
}

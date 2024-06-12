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


package com.io7m.garriga.main.commands;

import com.io7m.garriga.main.server.GServer;
import com.io7m.garriga.main.server.GServerConfiguration;
import com.io7m.quarrel.core.QCommandContextType;
import com.io7m.quarrel.core.QCommandMetadata;
import com.io7m.quarrel.core.QCommandStatus;
import com.io7m.quarrel.core.QCommandType;
import com.io7m.quarrel.core.QParameterNamed1;
import com.io7m.quarrel.core.QParameterNamedType;
import com.io7m.quarrel.core.QStringType;
import com.io7m.quarrel.ext.logback.QLogback;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

/**
 * Run the server.
 */

public final class GCmdServer implements QCommandType
{
  private final QCommandMetadata metadata;

  private static final QParameterNamed1<Path> CONFIG_FILE =
    new QParameterNamed1<>(
      "--configuration-file",
      List.of(),
      new QStringType.QConstant("The configuration file."),
      Optional.empty(),
      Path.class
    );

  /**
   * Run the server.
   */

  public GCmdServer()
  {
    this.metadata = new QCommandMetadata(
      "server",
      new QStringType.QConstant("Run the server."),
      Optional.empty()
    );
  }

  @Override
  public List<QParameterNamedType<?>> onListNamedParameters()
  {
    return QLogback.plusParameters(List.of(CONFIG_FILE));
  }

  @Override
  public QCommandStatus onExecute(
    final QCommandContextType context)
    throws Exception
  {
    SLF4JBridgeHandler.install();

    QLogback.configure(context);

    final GServerConfiguration configuration;
    try (var stream =
           Files.newInputStream(context.parameterValue(CONFIG_FILE))) {
      configuration = GServerConfiguration.open(stream);
    }

    try (var server = GServer.create(configuration)) {
      server.start();

      while (true) {
        Thread.sleep(1_000L);
      }
    }
  }

  @Override
  public QCommandMetadata metadata()
  {
    return this.metadata;
  }
}

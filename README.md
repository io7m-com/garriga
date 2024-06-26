garriga
===

[![Maven Central](https://img.shields.io/maven-central/v/com.io7m.garriga/com.io7m.garriga.svg?style=flat-square)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.io7m.garriga%22)
[![Maven Central (snapshot)](https://img.shields.io/nexus/s/com.io7m.garriga/com.io7m.garriga?server=https%3A%2F%2Fs01.oss.sonatype.org&style=flat-square)](https://s01.oss.sonatype.org/content/repositories/snapshots/com/io7m/garriga/)
[![Codecov](https://img.shields.io/codecov/c/github/io7m-com/garriga.svg?style=flat-square)](https://codecov.io/gh/io7m-com/garriga)
![Java Version](https://img.shields.io/badge/21-java?label=java&color=e6c35c)

![com.io7m.garriga](./src/site/resources/garriga.jpg?raw=true)

| JVM | Platform | Status |
|-----|----------|--------|
| OpenJDK (Temurin) Current | Linux | [![Build (OpenJDK (Temurin) Current, Linux)](https://img.shields.io/github/actions/workflow/status/io7m-com/garriga/main.linux.temurin.current.yml)](https://www.github.com/io7m-com/garriga/actions?query=workflow%3Amain.linux.temurin.current)|
| OpenJDK (Temurin) LTS | Linux | [![Build (OpenJDK (Temurin) LTS, Linux)](https://img.shields.io/github/actions/workflow/status/io7m-com/garriga/main.linux.temurin.lts.yml)](https://www.github.com/io7m-com/garriga/actions?query=workflow%3Amain.linux.temurin.lts)|
| OpenJDK (Temurin) Current | Windows | [![Build (OpenJDK (Temurin) Current, Windows)](https://img.shields.io/github/actions/workflow/status/io7m-com/garriga/main.windows.temurin.current.yml)](https://www.github.com/io7m-com/garriga/actions?query=workflow%3Amain.windows.temurin.current)|
| OpenJDK (Temurin) LTS | Windows | [![Build (OpenJDK (Temurin) LTS, Windows)](https://img.shields.io/github/actions/workflow/status/io7m-com/garriga/main.windows.temurin.lts.yml)](https://www.github.com/io7m-com/garriga/actions?query=workflow%3Amain.windows.temurin.lts)|

## garriga

An [AlertManager](https://prometheus.io/docs/alerting/latest/overview/) webhook
used to allow posting alert messages to a [Matrix](https://matrix.org/) channel.

## Features

* Post AlertManager alerts to a Matrix channel.
* Works with [Prometheus](https://prometheus.io/docs/alerting/latest/overview/).
* Works with [Grafana Mimir](https://grafana.com/docs/mimir/latest/references/architecture/components/alertmanager/).
* [OSGi-ready](https://www.osgi.org/)
* [JPMS-ready](https://en.wikipedia.org/wiki/Java_Platform_Module_System)
* ISC license.

## OCI

OCI images are available at [quay.io](https://quay.io/repository/io7mcom/garriga).

## Usage

Provide a configuration file providing the various required details. For
example, assume the following content in a file named `config.json`:

```
{
  "%schema": "urn:com.io7m.garriga:configuration:1",

  "HTTPServer": {
    "ListenAddress": "::",
    "ListenPort": 6000,
    "AuthenticationToken": "some-random-token-value"
  },

  "MatrixClient": {
    "BaseURI": "https://matrix.example.com",
    "User": "@alerts:matrix.example.com",
    "Password": "changeit",
    "Channel": "#alerts:matrix.example.com"
  }
}
```

Run `garriga server --configuration-file config.json`. The server will
bind to all addresses, listening on port `6000`, and will attempt to connect
to the given Matrix server using the provided credentials. Anyone talking
to the server will be required to supply an
`Authorization: Bearer some-random-token-value` header.

### WebHook

The server exposes an endpoint at `/4/send` that expects `POST` requests
supplying data in the version 4 [AlertManager](https://prometheus.io/docs/alerting/latest/configuration/#webhook_config)
format.

An example of a real-life message is as follows:

```
{
  "receiver": "matrix-webhook",
  "status": "firing",
  "alerts": [
    {
      "status": "firing",
      "labels": {
        "alertname": "HostFilesystemDeviceError",
        "device": "srv/grafana01/matrix_forwarder/etc",
        "fstype": "zfs",
        "instance": "srv.example.com:9100",
        "job": "dns-discovery",
        "mountpoint": "/srv/grafana01/matrix_forwarder/etc",
        "severity": "critical"
      },
      "annotations": {
        "description": "srv.example.com:9100: Device error with the /srv/grafana01/matrix_forwarder/etc filesystem\n  VALUE = 1\n  LABELS = map[__name__:node_filesystem_device_error device:srv/grafana01/matrix_forwarder/etc fstype:zfs instance:srv.example.com:9100 job:dns-discovery mountpoint:/srv/grafana01/matrix_forwarder/etc]",
        "summary": "Host filesystem device error (instance srv.example.com:9100)"
      },
      "startsAt": "2024-06-13T17:06:10.763Z",
      "endsAt": "0001-01-01T00:00:00Z",
      "generatorURL": "/graph?g0.expr=node_filesystem_device_error%7Bfstype%3D~%22ext2%7Cext4%7Czfs%7Cxfs%7Cvfat%22%7D+%3D%3D+1\u0026g0.tab=1",
      "fingerprint": "530731b28264424f"
    },
    {
      "status": "resolved",
      "labels": {
        "alertname": "HostSwapIsFillingUp",
        "instance": "w01.example.com:9100",
        "job": "dns-discovery",
        "nodename": "w01",
        "severity": "warning"
      },
      "annotations": {
        "description": "Swap is filling up (\u003e80%)\n  VALUE = 98.72484146349024\n  LABELS = map[instance:w01.example.com:9100 job:dns-discovery nodename:workstation01]",
        "summary": "Host swap is filling up (instance w01.example.com:9100)"
      },
      "startsAt": "2024-06-13T11:53:10.763Z",
      "endsAt": "0001-01-01T00:00:00Z",
      "generatorURL": "/graph?g0.expr=%28%281+-+%28node_memory_SwapFree_bytes+%2F+node_memory_SwapTotal_bytes%29%29+%2A+100+%3E+80%29+%2A+on+%28instance%29+group_left+%28nodename%29+node_uname_info%7Bnodename%3D~%22.%2B%22%7D\u0026g0.tab=1",
      "fingerprint": "983929006a0177d3"
    }
  ],
  "groupLabels": {},
  "commonLabels": {
    "job": "dns-discovery"
  },
  "commonAnnotations": {},
  "externalURL": "http://localhost:8080/alertmanager",
  "version": "4",
  "groupKey": "{}/{}:{}",
  "truncatedAlerts": 0
}
```


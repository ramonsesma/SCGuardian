# SCGuardian

`SCGuardian` is a portable `sclang`-only Quark for three jobs:

1. inspect the local SuperCollider runtime
2. resolve missing capabilities through explicit fallback policy
3. recover guardian-managed resources safely

## Quick Start

```supercollider
SCGuardian.inspect((
    classes: [\Object, \MdaPiano],
    ugens: [\SinOsc, \PV_BinShift]
));
```

```supercollider
SCGuardian.resolve(
    (role: \piano),
    (
        fallbackChains: (
            piano: [\MdaPiano, \RhodesVoice, \default]
        )
    ),
    SCGuardian.inspect
);
```

```supercollider
SCGuardian.protect({
    41 + 1
}, (
    cleanupLevel: \soft
));
```

## Install

```supercollider
Quarks.install("https://github.com/studio-sesma/SCGuardian");
```

## Public API

- `SCGuardian.inspect(request)` returns a structured capability report.
- `SCGuardian.resolve(spec, policy, report)` resolves a request against a report and policy.
- `SCGuardian.protect(func, policy)` runs code with explicit inspection and cleanup.
- `SCGuardian.registry` exposes the guardian-managed registry.
- `SCGuardian.recover(level)` runs `soft`, `medium`, or `hard` recovery.
- `SCGuardian.report(payload)` formats a readable summary string.

## Test

Run from the repository root:

```powershell
& 'C:\Program Files\SuperCollider-3.14.1\sclang.exe' -D -r -s --include-path 'Classes' --include-path 'tests' 'tests\RunSCGuardian.scd'
```

Or inside sclang after loading the Quark classes:

```supercollider
TestSCGuardian.run;
```

License: MIT.

// Run from sclang with: TestSCGuardian.run

TestSCGuardian : UnitTest {

    test_quark_facade_exists {
        var inspectResult;
        var resolveResult;
        var protectResult;
        var recoverResult;
        var reportResult;

        this.assert(SCGuardian.notNil);
        this.assert(SCGuardian.respondsTo(\inspect));
        this.assert(SCGuardian.respondsTo(\resolve));
        this.assert(SCGuardian.respondsTo(\protect));
        this.assert(SCGuardian.respondsTo(\recover));
        this.assert(SCGuardian.respondsTo(\report));

        inspectResult = SCGuardian.inspect((classes: [\Object]));
        resolveResult = SCGuardian.resolve(
            (role: \demo),
            (fallbackChains: (demo: [\Object]), allowFallback: true),
            inspectResult
        );
        protectResult = SCGuardian.protect({ 42 });
        recoverResult = SCGuardian.recover;
        reportResult = SCGuardian.report((ok: true));

        this.assert(inspectResult.isKindOf(IdentityDictionary));
        this.assertEquals(resolveResult[\request][\role], \demo);
        this.assertEquals(resolveResult[\chosen], \Object);
        this.assertEquals(protectResult[\ok], true);
        this.assertEquals(protectResult[\value], 42);
        this.assertEquals(recoverResult[\level], \soft);
        this.assert(reportResult.isString);
    }

    test_inspect_returns_stable_sections {
        var report;

        report = SCGuardian.inspect;

        this.assert(report.includesKey(\environment));
        this.assert(report.includesKey(\classes));
        this.assert(report.includesKey(\ugens));
        this.assert(report.includesKey(\quarks));
        this.assert(report.includesKey(\server));
        this.assert(report.includesKey(\warnings));
        this.assert(report.includesKey(\errors));
    }

    test_inspect_reports_requested_classes_and_ugens {
        var report;

        report = SCGuardian.inspect((
            classes: [\Object, \DefinitelyMissingClass],
            ugens: [\SinOsc, \DefinitelyMissingUGen]
        ));

        this.assert(report[\classes][\Object][\available] == true);
        this.assert(report[\classes][\DefinitelyMissingClass][\available] == false);
        this.assert(report[\ugens][\SinOsc][\available] == true);
        this.assert(report[\ugens][\DefinitelyMissingUGen][\available] == false);
    }

    test_resolve_uses_first_available_fallback {
        var report;
        var policy;
        var result;

        report = (
            classes: (
                MdaPiano: (available: false),
                RhodesVoice: (available: true),
                default: (available: true)
            )
        );

        policy = (
            fallbackChains: (
                piano: [\MdaPiano, \RhodesVoice, \default]
            )
        );

        result = SCGuardian.resolve((role: \piano), policy, report);

        this.assertEquals(result[\chosen], \RhodesVoice);
        this.assert(result[\degraded] == true);
        this.assertEquals(result[\warnings].size, 1);
        this.assertEquals(result[\warnings][0], "fallback applied");
    }

    test_resolve_fails_when_fallback_forbidden {
        var report;
        var policy;

        report = (classes: (MdaPiano: (available: false)));
        policy = (
            fallbackChains: (piano: [\MdaPiano]),
            allowFallback: false
        );

        this.assertException({
            SCGuardian.resolve((role: \piano), policy, report)
        }, Error, "forbidden fallback should throw");
    }

    test_registry_tracks_managed_resources {
        var registry;

        registry = SCGuardian.registry;
        registry.clear;
        registry.add(\clocks, \demoClock, { });

        this.assert(registry.categories.includes(\clocks));
        this.assert(registry.entriesFor(\clocks).size == 1);
    }

    test_recover_soft_stops_only_soft_resources {
        var softDisposed = false;
        var mediumDisposed = false;

        SCGuardian.registry.clear;
        SCGuardian.registry.add(\clocks, \demoClock, { softDisposed = true });
        SCGuardian.registry.add(\buffers, \demoBuffer, { mediumDisposed = true });

        SCGuardian.recover(\soft);

        this.assert(softDisposed == true);
        this.assert(mediumDisposed == false);
    }

    test_protect_runs_function_and_returns_report {
        var result;

        result = SCGuardian.protect({
            41 + 1
        }, (cleanupLevel: \soft));

        this.assert(result[\ok] == true);
        this.assert(result[\value] == 42);
        this.assert(result.includesKey(\report));
        this.assert(result.includesKey(\cleanup));
    }

    test_protect_captures_runtime_failure {
        var result;

        result = SCGuardian.protect({
            Error("boom").throw;
        }, (cleanupLevel: \soft));

        this.assert(result[\ok] == false);
        this.assertEquals(result[\errorType], \runtime_failure);
        this.assert(result.includesKey(\errorMessage));
        this.assert(result.includesKey(\report));
        this.assert(result.includesKey(\cleanup));
    }

    test_documentation_smoke_check {
        var root;

        root = [".", "..", "quarks/SCGuardian"].detect({ |candidate|
            File.exists(candidate +/+ "README.md")
        });

        this.assert(root.notNil);
        this.assert(File.exists(root +/+ "README.md"));
        this.assert(File.exists(root +/+ "HelpSource/Classes/SCGCapabilities.schelp"));
        this.assert(File.exists(root +/+ "HelpSource/Classes/SCGFallbacks.schelp"));
        this.assert(File.exists(root +/+ "HelpSource/Classes/SCGRecovery.schelp"));
        this.assert(File.exists(root +/+ "HelpSource/Classes/SCGuardian.schelp"));
        this.assert(File.exists(root +/+ "assets/supercollider-quarks-cover.png"));
        this.assert(File.exists(root +/+ "examples/quickstart.scd"));
    }

    test_inspect_resolve_protect_round_trip {
        var report;
        var result;

        report = SCGuardian.inspect((classes: [\Object]));
        result = SCGuardian.protect({
            SCGuardian.resolve(
                (role: \piano),
                (fallbackChains: (piano: [\Object])),
                report
            )
        }, (cleanupLevel: \soft));

        this.assert(result[\ok] == true);
        this.assert(result[\value][\chosen] == \Object);
    }
}

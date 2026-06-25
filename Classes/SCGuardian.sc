SCGuardian {
    *inspect { |request| ^SCGCapabilities.inspect(request) }
    *resolve { |spec, policy, report|
        ^SCGFallbacks.resolve(spec, policy, report ? this.inspect)
    }
    *protect { |func, policy|
        var report;
        var cleanupLevel;
        var cleanup;

        report = this.inspect;
        cleanupLevel = (policy ? IdentityDictionary.new)[\cleanupLevel] ? \soft;

        ^try {
            var value;

            value = func.value;
            cleanup = this.recover(cleanupLevel);
            (
                ok: true,
                value: value,
                report: report,
                cleanup: cleanup
            )
        } {
            |error|
            cleanup = this.recover(cleanupLevel);
            (
                ok: false,
                errorType: \runtime_failure,
                errorMessage: error.asString,
                report: report,
                cleanup: cleanup
            )
        }
    }
    *registry { ^SCGRecovery.registry }
    *recover { |level = \soft| ^SCGRecovery.recover(level) }
    *report { |payload| ^"SCGuardian report: %".format(payload.asCompileString) }
}

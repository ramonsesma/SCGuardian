SCGCapabilities {
    *inspect { |request|
        var report;
        var requested;
        var classes;
        var ugens;
        var classNames;
        var ugenNames;

        requested = request ? IdentityDictionary.new;
        classNames = requested[\classes] ? Array.new;
        ugenNames = requested[\ugens] ? Array.new;
        classes = IdentityDictionary.new;
        ugens = IdentityDictionary.new;

        classNames.do { |name|
            var sym;

            sym = name.asSymbol;
            classes[sym] = (
                requested: sym,
                available: sym.asClass.notNil
            );
        };

        ugenNames.do { |name|
            var sym;

            sym = name.asSymbol;
            ugens[sym] = (
                requested: sym,
                available: sym.asClass.notNil
            );
        };

        report = IdentityDictionary.new;
        report[\environment] = (
            sclangVersion: Main.version.asString,
            platform: Platform.name.asString
        );
        report[\classes] = classes;
        report[\ugens] = ugens;
        report[\quarks] = IdentityDictionary.new;
        report[\server] = (
            name: Server.default.name,
            booted: Server.default.serverRunning
        );
        report[\warnings] = Array.new;
        report[\errors] = Array.new;

        ^report
    }
}

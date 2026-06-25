SCGRecovery {
    classvar <registry;

    *initClass {
        registry = SCGRegistry.new;
    }

    *recover { |level = \soft|
        var touched = List.new;
        var categories;

        categories = switch(level,
            \soft, { [\routines, \clocks, \watchers] },
            \medium, { [\routines, \clocks, \watchers, \synths, \groups, \buffers, \buses] },
            { [\routines, \clocks, \watchers, \synths, \groups, \buffers, \buses, \servers] }
        );

        categories.do { |category|
            registry.entriesFor(category).do { |entry|
                entry[\disposeFunc].value;
                touched.add((category: category, id: entry[\id]));
            };
        };

        ^(level: level, recovered: touched.asArray)
    }
}

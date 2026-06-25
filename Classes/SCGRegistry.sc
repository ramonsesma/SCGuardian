SCGRegistry {
    var <storage;

    *new {
        ^super.new.init
    }

    init {
        storage = IdentityDictionary[
            \buffers -> List.new,
            \buses -> List.new,
            \synths -> List.new,
            \groups -> List.new,
            \routines -> List.new,
            \clocks -> List.new,
            \servers -> List.new,
            \watchers -> List.new
        ];
        ^this
    }

    add { |category, id, disposeFunc|
        var entries = storage[category];

        if(entries.isNil) {
            entries = List.new;
            storage[category] = entries;
        };

        entries.add((id: id, disposeFunc: disposeFunc));
        ^this
    }

    clear {
        storage.do { |entries|
            entries.clear;
        };
        ^this
    }

    categories {
        ^storage.keys.asArray
    }

    entriesFor { |category|
        ^(storage[category] ? List.new).copy
    }
}

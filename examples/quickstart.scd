// SCGuardian quickstart
// Inspect runtime capabilities and resolve a fallback chain.
(
~report = SCGuardian.inspect((
    classes: [\Object, \MdaPiano],
    ugens: [\SinOsc, \PV_BinShift]
));
SCGuardian.report(~report).postln;

SCGuardian.resolve(
    (role: \piano),
    (fallbackChains: (piano: [\MdaPiano, \RhodesVoice, \default])),
    ~report
).postln;
)

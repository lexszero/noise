s.boot;
(
{ | dur = 0.15, atk = 0.005, amp = 0.2 |
	SinOsc.ar(
		EnvGen.ar( Env(
			NamedControl.kr(\freq_l, [2960, 70, 90]),
			NamedControl.kr(\freq_d, [0.07, 0.2]),
			NamedControl.kr(\freq_c, [-13, -1]),
		) ),
		mul:amp * EnvGen.ar(
			Env.perc( atk, dur - atk, curve: NamedControl.kr(\amp_c, [-1, 6])),
			doneAction: 2
		);	
	) ! 2;
}.asSynthDef(name: "kick").add;

{ | atk = 0.01, dur = 0.15, freq = 50, amp=0.8 |
	BPF.ar(LFSaw.ar(freq), freq, 2, mul: EnvGen.kr( Env.perc( atk, dur-atk, amp, 6 ), doneAction: 2 )) ! 2;
}.asSynthDef(name: "bass").add;

{ | atk = 0.01, dur = 0.15, freq = 200, amp=0.8 |
	SinOsc.ar(LFTri.kr(freq/10, 0, freq, freq+100), 0, 0.7,
	mul: EnvGen.kr( Env.perc( atk, dur-atk, amp, 6 ), doneAction: 2 )) ! 2;
}.asSynthDef(name: "lead").add;

{ | atk = 0.01, dur = 0.15, freq = 200, amp=0.8 |
	PMOsc.ar(MouseY.kr(100,500), freq, MouseX.kr(0,20), 0, 0.1,
	mul: EnvGen.kr(Env.perc(atk, dur-atk-0.01, amp, 2), doneAction: 2)) ! 2;
}.asSynthDef(name: "lead1").add;

)

(
SynthDef(\reverb12, {arg inbus=0, outbus=0, predelay=0.048, combdecay=5, allpassdecay=1, revVol=0.31;
	var sig, y, z;
	sig = In.ar(inbus, 2); 
	z = DelayN.ar(sig, 0.1, predelay); // max 100 ms predelay
	y = Mix.ar(Array.fill(7,{ CombL.ar(z, 0.05, rrand(0.03, 0.05), combdecay) })); 
	6.do({ y = AllpassN.ar(y, 0.050, rrand(0.03, 0.05), allpassdecay) });
	Out.ar(outbus, sig + (y * revVol)); 
}).load(s); 
)
/*
(
{ | dur = 0.15, freq = 200, amp = 0.4 |
*/	

(
~ld = 1/4;
c = TempoClock(160/60);
m = Ppar([
	Pbind(*[
		instrument: \kick,
		delta: 1,
		dur: Pfunc({ thisThread.clock.beatDur }) / 4,
		amp: Pseq([
			Pseq([1], 16),
			Pseq([0.8, 0.8, 0.9, 1], 8)
		], inf) * 0.8,
	]),
	Pbind(*[
		instrument: \bass,
		delta: 1/4,
		dur: Pkey(\delta) * Pfunc({ thisThread.clock.beatDur }),
		freq: Pseq([
			Pseq([\r, 50,60,70], 16),
			Pseq([\r, 50,60,50], 12),
			Pseq([\r, 50,70,\r], 4)
		], inf),
		amp:0.6
	]),
	Pbind(*[
		instrument: \lead1,
		delta: 1/2,
		dur: Pkey(\delta) * Pfunc({ thisThread.clock.beatDur }),
		freq: Pseq([
			Pseq([\r], 4),				// 4
			Pseq([60, 70, 85, 70], 2),	// 8
			Pseq([127, 126, 124, 122], 2),// 8
			Pseq([\r, 40, 42, 38], 2),	// 8
			Pseq([\r], 4),				// 4
			// 32
			Pseq([\r, 90, \r, 95], 2),	// 8
			Pseq([\r, 40, 42, 38], 2),	// 8
			Pseq([127, 126, 124, 122], 2),// 8
			Pseq([\r], 8),				// 8

		], inf).midicps,
		amp: 0.3,
	]),
]).play(c, quant:[1])
)



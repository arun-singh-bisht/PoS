package com.posfone.promote.posfone.features.sip;

import android.net.sip.SipAudioCall;

public class SipCallListenr extends SipAudioCall.Listener {


    @Override
    public void onCalling(SipAudioCall call) {
        super.onCalling(call);
    }

    @Override
    public void onCallEstablished(SipAudioCall call) {
        call.startAudio();
        call.setSpeakerMode(true);
        call.toggleMute();
    }

    @Override
    public void onCallBusy(SipAudioCall call) {
        super.onCallBusy(call);
    }

    @Override
    public void onCallEnded(SipAudioCall call) {
        super.onCallEnded(call);
    }

}

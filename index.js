import { NativeEventEmitter, NativeModules } from 'react-native';

const { RNFileStack } = NativeModules;

const RNFileStackEmitter = new NativeEventEmitter(RNFileStack);

export default {
    upload(apiKey, uri) {
        return RNFileStack.upload(apiKey, uri);
    },
    emitter: RNFileStackEmitter
};

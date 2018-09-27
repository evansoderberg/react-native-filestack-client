# react-native-filestack-client

## Getting started

`$ npm install react-native-filestack-client --save`

### Mostly automatic installation iOS

### iOS

`$ react-native link react-native-filestack-client`

1. FilestackSDK and its dependencies (CryptoSwift & Almofire) must be installed.
   It's up to you to include them in your project. You can install via your
   Podfile, for example. I've included the .framework files in ios/Libraries
   for convenience.
2. Add an empty .swift file to your project, which will launch a prompt to create
   a bridging header file.

### Manual installation

#### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `react-native-filestack-client` and add `RNFilestack.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libRNFilestack.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Follow steps 1 & 2 in Mostly automatic installation iOS.
5. Run your project (`Cmd+R`)<

#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`

- Add `import com.evansoderberg.rnfilestack.RNFilestackPackage;` to the imports at the top of the file
- Add `new RNFilestackPackage()` to the list returned by the `getPackages()` method

2. Append the following lines to `android/settings.gradle`:
   ```
   include ':react-native-filestack-client'
   project(':react-native-filestack-client').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-filestack-client/android')
   ```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:

   ```
     compile project(':react-native-filestack-client')
   ```

4. `android/app/src/main/java/com/yourapp/MainApplication.java`

   ```
      public class MainApplication extends Application implements ReactApplication {

      private static Context context;

      public static Context getAppContext() {
        return MainApplication.context;
      }

      @Override
      public void onCreate() {
        super.onCreate();
        MainApplication.context = getApplicationContext();
      }
    }
   ```

## Usage

```javascript
import RNFilestack from "react-native-filestack-client";

const FILE_STACK_KEY = "YOUR-FILESTACK-KEY";

class App extends React.Component {
  componentWillUnmount() {
    RNFileStack.emitter.removeAllListeners("onProgress");
    RNFileStack.emitter.removeAllListeners("onFinish");
  }

  componentDidMount() {
    RNFileStack.emitter.addListener("onProgress", progress => {
      this.onAttachmentUploadProgress(progress);
    });
    RNFileStack.emitter.addListener("onFinish", data => {
      this.onAttachmentUploadFinished(data);
    });
  }

  onAttachmentUploadProgress = data => {
    if (data.error) {
      this.setState({
        progress: null
      });
      console.warn(data.error);
    }
    this.setState({
      progress: data.progress
    });
  };

  onAttachmentUploadFinished = data => {
    this.setState({
      progress: null,
      attachments: [
        {
          fileName: data.fileName,
          fileRef: data.fileRef
        }
      ]
    });
  };

  uploadFile = () => {
    RNFileStack.upload(FILE_STACK_KEY, this.state.fileURI);
  };

  render() {
    return <Button onPress={this.uploadFile}>Upload</Button>;
  }
}

// TODO: What to do with the module?
RNFilestack;
```

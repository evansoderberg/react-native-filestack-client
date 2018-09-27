import Foundation
import FilestackSDK

@objc(RNFileStack)
class RNFileStack : RCTEventEmitter {
    override func supportedEvents() -> [String]! {
        return ["upload", "onProgress", "onFinish"]
    }
    
    
    @objc func onProgress(_ progress: Progress) -> Void {
        let ret : [String: Double] = [
            "progress": progress.fractionCompleted
        ]
        self.sendEvent(withName: "onProgress", body: ret)
    }
    
    
    @objc func onFinish(_ fileName: String, fileURL: String, fileRef: String) -> Void {
        print("onFinish")
        print(fileName)
        print(fileURL)
        let ret : [String: String] = [
            "fileName": fileName,
            "fileURL": fileURL,
            "fileRef": fileRef
        ]
        self.sendEvent(withName: "onFinish", body: ret)
    }
    
    @objc func onError(_ error: String) -> Void {
        let ret : [String: String] = [
            "error": error
        ]
        self.sendEvent(withName: "onProgress", body: ret)
    }
    
    
    @objc func upload(_ apiKey: String, fileURI: URL) -> Void {
        print()
        let oneDayInSeconds: TimeInterval = 60 * 60 * 24 // expires tomorrow
        let policy = Policy(// Set your expiry time (24 hours in our case)
            expiry: Date(timeIntervalSinceNow: oneDayInSeconds),
            call: [.pick, .read, .store])
        guard let security = try? Security(policy: policy, appSecret: apiKey) else {
            return
        }
        let client = Client(apiKey: apiKey, security: security)
        let uploadProgress: (Progress) -> Void = { progress in
            self.onProgress(progress)
        }
        
        let multiPartUpload = client.multiPartUpload(
            from: fileURI,
            useIntelligentIngestionIfAvailable: true,
            queue: .main,
            startUploadImmediately: true,
            // Set your upload progress handler here (optional)
        uploadProgress: uploadProgress) { response in
            // Try to obtain Filestack handle
            if let json = response.json, let handle = json["handle"] as? String {
                // Use Filestack handle
                if (json["url"] != nil) {
                    self.onFinish(json["filename"] as! String, fileURL: json["url"] as! String, fileRef: json["handle"] as! String)
                }
            } else if let error = response.error {
                // TODO: Better error handling. Have to figure out what the possible
                // errors are in order to cast to string.
                self.onError("Upload failed")
            }
        }
    }
    
}

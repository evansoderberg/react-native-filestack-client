
Pod::Spec.new do |s|
  s.name         = "RNFilestack"
  s.version      = "1.0.0"
  s.summary      = "RNFilestack"
  s.description  = <<-DESC
                  RNFilestack
                   DESC
  s.homepage     = ""
  s.license      = "MIT"
  # s.license      = { :type => "MIT", :file => "FILE_LICENSE" }
  s.author             = { "author" => "evan.soderberg@myagi.com" }
  s.platform     = :ios, "9.0"
  s.source       = { :git => "https://github.com/evansoderberg/react-native-filestack-client", :tag => "master" }
  s.source_files  = "RNFilestack/**/*.{h,m}"
  s.requires_arc = true


  s.dependency "React"
  s.dependency 'Filestack', '~> 1.5'

end

  
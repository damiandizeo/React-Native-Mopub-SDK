require "json"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))

Pod::Spec.new do |s|
    s.name         = "react-native-mopub-sdk"
    s.version      = package["version"]
    s.summary      = package["description"]
    s.description  = <<-DESC
    react-native-mopub-sdk
    DESC
    s.homepage     = "https://github.com/damiandizeo/React-Native-Mopub-SDK.git"
    s.license      = "MIT"
    # s.license    = { :type => "MIT", :file => "FILE_LICENSE" }
    s.author       = { "author" => "author@domain.cn" }
    s.platform     = :ios, "10.0"
    s.source       = { :git => "https://github.com/damiandizeo/React-Native-Mopub-SDK.git", :tag => "#{s.version}" }
    
    s.source_files = "ios/**/*.{h,m}"
    s.requires_arc = true
    
    s.dependency "React"
    
    s.subspec "MoPub" do |ss|
        
        ss.dependency 'mopub-ios-sdk', '~> 5.0'
        
        s.static_framework = true
    end
    

    s.subspec "AdMob" do |ss|
        
        ss.dependency 'MoPub-AdMob-Adapters', '~> 7.68.0.1'
        
    end

    s.subspec "FacebookAudienceNetwork" do |ss|
        
        ss.dependency 'MoPub-FacebookAudienceNetwork-Adapters', '~> 6.2.0.1'
        
    end
        
    s.subspec "Vungle" do |ss|
        
        ss.dependency 'MoPub-Vungle-Adapters', '~> 6.8.1.0'
        
    end

    s.subspec "IronSource" do |ss|
        
        ss.dependency 'MoPub-IronSource-Adapters', '~> 7.0.3.0.2'
        
    end

end


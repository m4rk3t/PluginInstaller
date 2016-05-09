
if [ ! -d android-ndk-r10e ]; then
	wget https://dl.google.com/android/repository/android-ndk-r10e-linux-x86_64.zip
	unzip android-ndk-r10e-linux-x86_64.zip
fi

if [ ! -d libav-avp ]; then
	git clone https://github.com/archos-sa/libav-avp.git -b mc-libav
fi

cd libav-avp
BUILD=DEBUG ../android-ndk-r10e/ndk-build -C ndk/full -j8
BUILD=DEBUG ../android-ndk-r10e/ndk-build -C ndk/full_no_neon -j8
BUILD=DEBUG ../android-ndk-r10e/ndk-build -C ndk/full_x86 -j8
#BUILD=DEBUG ../android-ndk-r10e/ndk-build -C ndk/full_x86_64 -j8
#BUILD=DEBUG ../android-ndk-r10e/ndk-build -C ndk/full_arm64 -j8


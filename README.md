# Midtrans Payment Gateway for Flutter

[![pub package](https://img.shields.io/pub/v/midpay.svg)](https://pub.dev/packages/midpay)
[![pub package](https://img.shields.io/twitter/follow/kakzaki_id.svg?colorA=1da1f2&colorB=&label=Follow%20on%20Twitter)](https://twitter.com/kakzaki_id)

## Platform Support

| Android | iOS | MacOS | Web | Linux | Windows |
| :-----: | :-: | :---: | :-: | :---: | :-----: |
|   ✔️  |    ✔️  |   ️X    |  ️X   |  ️ X    |   ️X     |



## Android setup

Add style to your android/app/src/main/res/values/styles.xml :
```
<style name="AppTheme" parent="Theme.AppCompat.Light.DarkActionBar">
    <item name="windowActionBar">false</item>
    <item name="windowNoTitle">true</item>
</style>
```
And full styles.xml will be like below :
```
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <style name="LaunchTheme" parent="@android:style/Theme.Black.NoTitleBar">
        <!-- Show a splash screen on the activity. Automatically removed when
             Flutter draws its first frame -->
        <item name="android:windowBackground">@drawable/launch_background</item>
    </style>
    <style name="AppTheme" parent="Theme.AppCompat.Light.DarkActionBar">
        <item name="windowActionBar">false</item>
        <item name="windowNoTitle">true</item>
    </style>
</resources>
```
And add the style to you Android Manifest in your application tag :
```
tools:replace="android:label"
android:theme="@style/AppTheme"
```
## IOS
No specific setup required

## BASE_URL
goto midtrans official documentation or [merchant server github](https://github.com/rizdaprasetya/midtrans-mobile-merchant-server--php-sample-)

If you like my content, please consider buying me a coffee. Thank you for your support!

[![ko-fi](https://ko-fi.com/img/githubbutton_sm.svg)](https://ko-fi.com/Z8Z6656JW)

<a href="https://www.buymeacoffee.com/QP1rCmf5L" target="_blank"><img src="https://www.buymeacoffee.com/assets/img/custom_images/orange_img.png" alt="Buy Me A Coffee" style="height: 41px !important;width: 174px !important;box-shadow: 0px 3px 2px 0px rgba(190, 190, 190, 0.5) !important;-webkit-box-shadow: 0px 3px 2px 0px rgba(190, 190, 190, 0.5) !important;" ></a>

https://saweria.co/kakzaki

require 'calabash-android/calabash_steps'

Given(/^that I am at the StartupActivity$/) do
  fail_msg="Missing elements for StartUpActivity"
  element_exists("webview") and element_exists("button") and element_exists("imageview") or fail(msg=fail_msg)
end

Given(/^an ad is at the bottom of the screen$/) do
  wv_element = "webview css:'#google_ads_frame1_anchor'"
  wv_non_exist_fail_msg = "WebView element does not exist"

  wait_for_elements_exist([wv_element], :timeout => 20) or fail(msg=wv_non_exist_fail_msg)
end

When(/^I press the ad$/) do
  wv_touch_fail_msg = "WebView is untouchable"

  wait_for_elements_exist(["webview css:'body'"], :timeout => 20)
  touch("webview css:'body'")["success"] or fail(msg=wv_touch_fail_msg)
end

Then(/^I should be taken to the browser$/) do
  # It's hard to test a browser that is not part of the app, not to mention that there are a lot of Android browsers. We'll just test for non existence here.
  wv_element = "webview css:'#google_ads_frame1_anchor'"
  fail_msg = "WebView still exists. Browser not launched."

  wait_for(:timeout => 20) { !element_exists(wv_element) } or fail(msg=fail_msg)
end

Given(/^that I am viewing the list of places near me$/) do
  pending # express the regexp above with the code you wish you had
end

When(/^I scroll to the bottom of the list$/) do
  pending # express the regexp above with the code you wish you had
end

Then(/^the last item's distance to me should not be greater than (\d+)m$/) do |arg1|
  pending # express the regexp above with the code you wish you had
end

When(/^I select the first item$/) do
  pending # express the regexp above with the code you wish you had
end

Then(/^I should see a map$/) do
  pending # express the regexp above with the code you wish you had
end

Then(/^I should see a list of thumbnails at the left side$/) do
  pending # express the regexp above with the code you wish you had
end

Then(/^I should see the sandwich icon at the top left of the screen$/) do
  pending # express the regexp above with the code you wish you had
end

When(/^I click the sandwich icon$/) do
  pending # express the regexp above with the code you wish you had
end

Then(/^I should see the side navigation$/) do
  pending # express the regexp above with the code you wish you had
end

Then(/^I should see "(.*?)" as the first item$/) do |arg1|
  pending # express the regexp above with the code you wish you had
end

When(/^I scroll down the side navigation$/) do
  pending # express the regexp above with the code you wish you had
end

Then(/^I should see "(.*?)" as the last item$/) do |arg1|
  pending # express the regexp above with the code you wish you had
end

Then(/^I should see a list of places near me$/) do
  pending # express the regexp above with the code you wish you had
end

And(/^I can see the "(.*?)" button$/) do |arg1|
  element_exists("button text:'#{arg1}'") or fail(msg="No such button #{arg1}")
end

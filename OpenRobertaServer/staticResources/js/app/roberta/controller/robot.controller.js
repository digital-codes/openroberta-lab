define(["require","exports","util.roberta","log","message","guiState.controller","robot.model","program.controller","configuration.controller","webview.controller","jquery","blockly","connection.controller","jquery-validate"],(function(e,o,t,n,a,i,r,s,l,d,c,g,m){var u,f,p;function b(e){f=e,c("#single-modal-list").modal("hide"),i.setRobotPort(e)}function h(e,o){return e<0?(n.info("The firmware version '"+o+"' on the server is newer than the firmware version '"+i.getRobotVersion()+"' on the robot"),c("#confirmUpdateFirmware").modal("show"),!0):e>0&&(n.info("The firmware version '"+o+"' on the server is older than the firmware version '"+i.getRobotVersion()+"' on the robot"),a.displayMessage("MESSAGE_FIRMWARE_ERROR","POPUP",""),!0)}function w(){r.updateFirmware((function(e){i.setState(e),"ok"===e.rc?a.displayMessage("MESSAGE_RESTART_ROBOT","POPUP",""):a.displayInformation(e,"",e.message,i.getRobotFWName())}))}function v(e,o){var t=i.getRobots();for(var n in Object.keys(t)){var a=t[n];if(a.name===e){for(var r=0,s=Object.entries(a.extensions);r<s.length;r++){var l=s[r],d=l[0];if("always"===l[1]){o[d]=!0;break}}break}}}Object.defineProperty(o,"__esModule",{value:!0}),o.switchRobot=o.updateFirmware=o.handleFirmwareConflict=o.showListModal=o.showScanModal=o.showSetApiKeyModal=o.showSetTokenModal=o.getPort=o.setPort=o.init=void 0,o.init=function(e,o){var t=c.Deferred();return v(e,o),c.when(r.setRobot(e,o,(function(t){"ok"==t.rc&&(i.setExtensions(o),i.setRobot(e,t,!0))}))).then((function(){c("#iconDisplayRobotState").onWrap("click",(function(){p.showRobotInfo()}),"display robot state"),c("#wlan-form").removeData("validator"),c.validator.addMethod("wlanRegex",(function(e,o){return this.optional(o)||/^[a-zA-Z0-9$ *\(\)\{\}\[\]><~`\'\\\/|=+!?.,%#+&^@_\-äöüÄÖÜß]+$/gi.test(e)}),"This field contains nonvalid symbols."),c("#wlan-form").validate({rules:{wlanSsid:{required:!0,wlanRegex:!0},wlanPassword:{required:!0,wlanRegex:!0}},errorClass:"form-invalid",errorPlacement:function(e,o){e.insertBefore(o.parent())},messages:{wlanSsid:{required:g.Msg.VALIDATION_FIELD_REQUIRED,wlanRegex:g.Msg.VALIDATION_CONTAINS_SPECIAL_CHARACTERS},wlanPassword:{required:g.Msg.VALIDATION_FIELD_REQUIRED,wlanRegex:g.Msg.VALIDATION_CONTAINS_SPECIAL_CHARACTERS}}}),c("#setWlanCredentials").onWrap("click",(function(e){e.preventDefault(),c("#wlan-form").validate(),c("#wlan-form").valid()&&(s.setSSID(document.getElementById("wlanSsid").value),s.setPassword(document.getElementById("wlanPassword").value),c("#menu-wlan").modal("hide"))}),"wlan form submitted"),c("#doUpdateFirmware").onWrap("click",(function(){c("#set-token").modal("hide"),c("#confirmUpdateFirmware").modal("hide"),w()}),"update firmware of robot"),u=c("#single-modal-form"),c("#connectionsTable").bootstrapTable({formatNoMatches:function(){return'<div class="lds-ellipsis"></div>'},columns:[{title:"Name",field:"name"},{visible:!1,field:"id"}]}),c("#connectionsTable").onWrap("click-row.bs.table",(function(e,o){d.jsToAppInterface({target:i.getRobot(),type:"connect",robot:o.id})}),"connect to robot"),c("#show-available-connections").on("hidden.bs.modal",(function(e){d.jsToAppInterface({target:i.getRobot(),type:"stopScan"})})),c("#show-available-connections").onWrap("add",(function(e,o){c("#connectionsTable").bootstrapTable("insertRow",{index:999,row:{name:o.brickname,id:o.brickid}})}),"insert robot connections"),c("#show-available-connections").onWrap("connect",(function(e,o){var t={};t.robotName=o.brickname,t.robotState="wait",i.setState(t),c("#show-available-connections").modal("hide")}),"connect to a robot"),t.resolve()})),t.promise()},o.setPort=b,o.getPort=function(){return f},o.showSetTokenModal=function(e,o){t.showSingleModal((function(){c("#singleModalInput").attr("type","text"),c("#single-modal h5").text(g.Msg.MENU_CONNECT),c("#single-modal label").text(g.Msg.POPUP_VALUE),c("#singleModalInput").addClass("capitalLetters"),c("#single-modal a[href]").text(g.Msg.POPUP_STARTUP_HELP),c("#single-modal a[href]").attr("href","http://wiki.open-roberta.org")}),(function(){var e;e=c("#singleModalInput").val().toUpperCase(),u.validate(),u.valid()&&r.setToken(e,(function(o){"ok"===o.rc?(i.setRobotToken(e),i.setState(o),a.displayInformation(o,"MESSAGE_ROBOT_CONNECTED",o.message,i.getRobotName()),h(o["robot.update"],o["robot.serverVersion"])):"ORA_TOKEN_SET_ERROR_WRONG_ROBOTTYPE"===o.message&&c(".modal").modal("hide"),t.response(o)}))}),(function(){c("#singleModalInput").removeClass("capitalLetters")}),{rules:{singleModalInput:{required:!0,minlength:e,maxlength:o}},errorClass:"form-invalid",errorPlacement:function(e,o){e.insertAfter(o)},messages:{singleModalInput:{required:g.Msg.VALIDATION_FIELD_REQUIRED,minlength:g.Msg.VALIDATION_TOKEN_LENGTH,maxlength:g.Msg.VALIDATION_TOKEN_LENGTH}}})},o.showSetApiKeyModal=function(e){t.showSingleModal((function(){c(".form-label-ip").removeClass("hidden"),c(".ip-input-group").removeClass("hidden"),c("#singleModalInput").attr("type","text"),c("#singleModalInputIp").attr("type","text"),c("#single-modal #singleModalInputIp").val(e),c("#single-modal h5").text(g.Msg.MENU_CONNECT),c("#single-modal .form-label").text(g.Msg.POPUP_VALUE),c("#single-modal .form-label-ip").text("URL or IP address"),c("#single-modal a[href]").text(g.Msg.POPUP_STARTUP_HELP),c("#single-modal a[href]").attr("href","http://wiki.open-roberta.org")}),(function(){var e,o;e=c("#singleModalInput").val(),o=c("#singleModalInputIp").val(),u.validate(),u.valid()&&function(e){return new RegExp("^txt40\\.local$|^192\\.168(\\.[0-9]{1,3}){2}$").test(e)}(o)&&r.setApiKey(e,o,(function(){i.setRobotToken(e),i.setRobotUrl(o),i.setRunEnabled(!0),i.setConnectionState("wait"),c("#stopProgram").addClass("disabled"),c("#head-navi-icon-robot").removeClass("error"),c("#head-navi-icon-robot").removeClass("busy"),c("#head-navi-icon-robot").addClass("wait"),a.displayInformation({rc:"ok"},"MESSAGE_ROBOT_CONNECTED","",i.getRobotName()),setTimeout((function(){c(".modal").modal("hide")}),100)}))}),(function(){c(".form-label-ip").addClass("hidden"),c(".ip-input-group").addClass("hidden")}),{rules:{singleModalInput:{required:!0,minlength:6,maxlength:6},singleModalInputIp:{required:!0}},errorClass:"form-invalid",errorPlacement:function(e,o){e.insertAfter(o)},messages:{singleModalInput:{required:g.Msg.VALIDATION_FIELD_REQUIRED,minlength:g.Msg.VALIDATION_TOKEN_LENGTH,maxlength:g.Msg.VALIDATION_TOKEN_LENGTH},singleModalInputIp:{required:"Wifi: txt40.local, USB: 192.168.7.2, or Ip address of your robot: INFO > Wi-FI > IP"}}})},o.showScanModal=function(){c("#show-available-connections").is(":visible")||(c("#connectionsTable").bootstrapTable("removeAll"),d.jsToAppInterface({target:i.getRobot(),type:"startScan"}),c("#show-available-connections").modal("show"))},o.showListModal=function(){t.showSingleListModal((function(){c("#single-modal-list h3").text(g.Msg.MENU_CONNECT),c("#single-modal-list label").text(g.Msg.POPUP_VALUE),c("#single-modal-list a[href]").text(g.Msg.POPUP_STARTUP_HELP),c("#single-modal-list a[href]").attr("href","http://wiki.open-roberta.org")}),(function(){b(document.getElementById("singleModalListInput").value)}),(function(){}))},o.handleFirmwareConflict=h,o.updateFirmware=w,o.switchRobot=function e(o,t,n,d){var u;function f(e,o){if(i.findGroup(e)!=i.getRobotGroup())return!1;var t=i.getExtensions(),n=Object.keys(o),a=Object.keys(i.getExtensions());if(n.length!==a.length)return!1;for(var r=0,s=n;r<s.length;r++){var l=s[r];if(o[l]!==t[l])return!1}return!0}s.SSID=null,s.password=null,document.getElementById("wlanSsid").value="",document.getElementById("wlanPassword").value="",v(o,t);var p=f(o,t);if(!n&&p?u=!0:(u=n||!1,g.clipboardXml_=null),u||i.isProgramSaved()&&i.isConfigurationSaved()){if(o===i.getRobot()&&p)return void("function"==typeof d&&d());r.setRobot(o,t,(function(e){if("ok"===e.rc){c(".rightMenuButton.rightActive").length>0&&c(".rightMenuButton.rightActive").clickWrap();var n=f(o,t);i.setExtensions(t),i.setRobot(o,e),n||(l.resetView(),s.resetView()),m.switchConnection(o),l.changeRobotSvg(),"tabConfList"==i.getView()&&c("#confList>.bootstrap-table").find('button[name="refresh"]').clickWrap(),"tabProgList"==i.getView()&&c("#progList>.bootstrap-table").find('button[name="refresh"]').clickWrap(),"function"==typeof d?d():a.displayInformation(e,e.message,e.message,i.getRobotRealName());var r=i.getRobotDeprecatedData(o);void 0!==r&&(c("#show-message>.modal-dialog").removeClass("modal-sm"),c("#show-message").on("hidden.bs.modal",(function(){c("#show-message>.modal-dialog").addClass("modal-sm")})),a.displayPopupMessage(i.getLanguage(),r,"OK",!1))}else a.displayInformation(e,e.message,e.message,i.getRobotRealName())}))}else c("#show-message-confirm").oneWrap("shown.bs.modal",(function(n){c("#confirm").off(),c("#confirm").onWrap("click",(function(n){n.preventDefault(),e(o,t,!0,d)}),"confirm modal"),c("#confirmCancel").off(),c("#confirmCancel").onWrap("click",(function(e){e.preventDefault(),c(".modal").modal("hide")}),"cancel modal")})),i.isUserLoggedIn()?a.displayMessage("POPUP_BEFOREUNLOAD_LOGGEDIN","POPUP","",!0):a.displayMessage("POPUP_BEFOREUNLOAD","POPUP","",!0)}}));
//# sourceMappingURL=robot.controller.js.map
//# sourceMappingURL=robot.controller.js.map

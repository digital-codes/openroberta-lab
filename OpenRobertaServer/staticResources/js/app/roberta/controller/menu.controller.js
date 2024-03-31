define(["require","exports","message","comm","wrap","user.controller","notification.controller","guiState.controller","program.controller","progRun.controller","configuration.controller","import.controller","tour.controller","sourceCodeEditor.controller","jquery","blockly","progTutorial.controller","util.roberta","connection.controller","slick"],(function(e,t,o,a,n,r,i,c,l,s,u,d,g,h,m,p,b,f,k){Object.defineProperty(t,"__esModule",{value:!0}),t.init=void 0;var w,v=0,P="?",S="&",y="=",W="forgotPassword",C="activateAccount",L="loadSystem",O="tutorial",R="gallery",_="tour",T="kiosk",N="exampleView",E="loadProgram",I="extensions";function M(e){var t,o=decodeURIComponent(document.location.toString()),a=o.indexOf("?"),n=o=o.substring(a+1),r=n.indexOf("<");r>=0&&(n=o.substring(0,r),t=o.substring(r));var i,c,l=n.split(S);for(c=0;c<l.length;c++)if((i=l[c].split(y))[0]===e)return void 0===i[1]||("loadProgram"===i[0]?t:i[1])}function A(){null!=k.getConnectionInstance()&&k.getConnectionInstance().terminate(),m(".fromRight.rightActive").length>0?m("#blocklyDiv").closeRightView((function(){return f.closeSimRobotWindow(),m("#tabStart").tabWrapShow()})):m("#tabStart").tabWrapShow()}t.init=function(e){e&&e instanceof Function?(w=e,m("#startupVersion").text(c.getServerVersion()),function(){m(".navbar-collapse a:not(.dropdown-toggle)").click((function(){m(".dropdown-menu.show").collapse("hide"),m(".navbar-collapse.show").collapse("hide")})),navigator.userAgent.match(/iPad/i)||navigator.userAgent.match(/iPhone/i)||(m('[rel="tooltip"]').not(".rightMenuButton").tooltip({container:"body",placement:"right",trigger:"hover"}),m('[rel="tooltip"].rightMenuButton').tooltip({container:"body",placement:"left",trigger:"hover"}));if(document.addEventListener("gesturestart",(function(e){e.preventDefault(),e.stopPropagation()})),m(".blocklyButtonBack, .blocklyWidgetDiv, #head-navigation, #main-section, #tutorial-navigation").on("mousedown touchstart keydown",(function(e){if(m(e.target).not(".blocklyTreeLabel, .blocklytreerow, .toolboxicon, .goog-palette-colorswatch, .goog-menu-vertical, .goog-menuitem-checkbox, div.goog-menuitem-content, div.goog-menuitem, img").length>0){if(m(e.target).filter(".blocklyHtmlInput").length>0&&!e.metaKey)return;p&&p.getMainWorkspace()&&p.hideChaff()}})),m(".modal").onWrap("shown.bs.modal",(function(){m(this).find("[autofocus]").focus()})),m(".navbar-collapse").on("click",".dropdown-menu a,.visible-xs",(function(e){m("#navbarCollapse").collapse("hide")})),m("#head-navigation-gallery").on("click","a,.visible-xs",(function(e){m("#navbarCollapse").collapse("hide")})),c.isPublicServerVersion()){var e='<div href="#" id="feedbackButton" class="rightMenuButton" rel="tooltip" data-bs-original-title="" title=""><span id="" class="feedbackButton typcn typcn-feedback"></span></div>';m("#rightMenuDiv").append(e),window.onmessage=function(e){if("closeFeedback"===e.data)m("#feedbackIframe").oneWrap("load",(function(){setTimeout((function(){m("#feedbackIframe").attr("src","about:blank"),m("#feedbackModal").modal("hide")}),1e3)}));else if(e.data.indexOf("feedbackHeight")>=0){var t=e.data.split(":")[1]||400;m("#feedbackIframe").height(t)}},m("#feedbackButton").on("click","",(function(e){m("#feedbackModal").on("show.bs.modal",(function(){"de"===c.getLanguage().toLowerCase()?m("#feedbackIframe").attr("src","https://www.roberta-home.de/lab/feedback/"):m("#feedbackIframe").attr("src","https://www.roberta-home.de/en/lab/feedback/")})),m("#feedbackModal").modal("show")}))}m("#head-navigation-program-edit").on("click",".dropdown-menu li:not(.disabled) a",(function(e){var t=function(e){switch(e.target.id||e.currentTarget.id){case"menuRunProg":s.runOnBrick();break;case"menuRunSim":m("#simButton").clickWrap();break;case"menuCheckProg":l.checkProgram();break;case"menuNewProg":l.newProgram();break;case"menuListProg":m("#tabProgList").data("type","Programs"),m("#tabProgList").tabWrapShow();break;case"menuListExamples":m("#tabProgList").data("type","Examples"),m("#tabProgList").tabWrapShow();break;case"menuSaveProg":l.saveToServer();break;case"menuSaveAsProg":l.showSaveAsModal();break;case"menuShowCode":m("#codeButton").clickWrap();break;case"menuSourceCodeEditor":h.clickSourceCodeEditor();break;case"menuExportProg":l.exportXml();break;case"menuExportAllProgs":l.exportAllXml();break;case"menuImportProg":d.importXml();break;case"menuLinkProg":l.linkProgram();break;case"menuToolboxBeginner":m('.levelTabs a[href="#beginner"]').tabWrapShow();break;case"menuToolboxExpert":m('.levelTabs a[href="#expert"]').tabWrapShow();break;case"menuDefaultFirmware":k.getConnectionInstance().reset2DefaultFirmware()}};n.wrapUI(t,"edit menu click")(e)})),m("#head-navigation-configuration-edit").onWrap("click",".dropdown-menu li:not(.disabled) a",(function(e){switch(m(".modal").modal("hide"),e.target.id||e.currentTarget.id){case"menuCheckConfig":o.displayMessage("MESSAGE_NOT_AVAILABLE","POPUP","");break;case"menuNewConfig":u.newConfiguration();break;case"menuListConfig":m("#tabConfList").tabWrapShow();break;case"menuSaveConfig":u.saveToServer();break;case"menuSaveAsConfig":u.showSaveAsModal()}}),"configuration edit clicked"),m("#head-navigation-robot").onWrap("click",".dropdown-menu li:not(.disabled) a",(function(e){m(".modal").modal("hide");var t=e.currentTarget.id;"menuConnect"===t?k.getConnectionInstance().showConnectionModal():"menuRobotInfo"===t?k.getConnectionInstance().showRobotInfo():"menuWlan"===t?k.getConnectionInstance().showWlanModal():"menuRobotSwitch"===t&&A()}),"robot clicked"),m("#head-navigation-help").onWrap("click",".dropdown-menu li:not(.disabled) a",(function(e){m(".modal").modal("hide");var t=e.target.id;"menuAbout"===t?(m("#version").text(c.getServerVersion()),m("#show-about").modal("show")):"menuLogging"===t&&m("#tabLogList").tabWrapShow()}),"help clicked"),m(".menuGeneral").onWrap("click",(function(e){window.open("https://jira.iais.fraunhofer.de/wiki/display/ORInfo")}),"head navigation menu item general clicked"),m(".menuFaq").onWrap("click",(function(e){window.open("https://jira.iais.fraunhofer.de/wiki/display/ORInfo/FAQ")}),"head navigation menu item faq clicked"),m(".menuAboutProject").onWrap("click",(function(e){"de"==c.getLanguage()?window.open("https://www.roberta-home.de/index.php?id=135"):window.open("https://www.roberta-home.de/index.php?id=135&L=1")}),"head navigation menu item about clicked"),m(".menuShowStart").onWrap("click",(function(){return A()})),m("#head-navigation-user").onWrap("click",".dropdown-menu li:not(.disabled) a",(function(e){switch(m(".modal").modal("hide"),e.target.id){case"menuUserGroupLogin":r.showUserGroupLoginForm();break;case"menuLogout":r.logout();break;case"menuGroupPanel":m("#tabUserGroupList").tabWrapShow();break;case"menuChangeUser":r.showUserDataForm();break;case"menuDeleteUser":r.showDeleteUserModal();break;case"menuStateInfo":r.showUserInfo();break;case"menuNotification":i.showNotificationModal()}return!1}),"user clicked"),m(".menuLogin").onWrap("click",(function(e){r.showLoginForm()}),"head navigation menu item login clicked"),m("#menuTabProgram").onWrap("click","",(function(e){m("#tabSimulation").hasClass("tabClicked")&&m(".scroller-left").clickWrap(),m(".scroller-left").clickWrap(),m("#tabProgram").tabWrapShow()}),"tabProgram clicked"),m("#head-navigation-gallery").onWrap("click",(function(e){return m("#tabGalleryList").tabWrapShow(),!1}),"gallery clicked"),m("#head-navigation-tutorial").onWrap("click",(function(e){return m("#tabTutorialList").tabWrapShow(),!1}),"tutorial clicked"),m("#menuTabConfiguration").onWrap("click","",(function(e){(m("#tabProgram").hasClass("tabClicked")||m("#tabConfiguration").hasClass("tabClicked"))&&m(".scroller-right").clickWrap(),m("#tabConfiguration").clickWrap()}),"tabConfiguration clicked"),m("#menuTabNN").onWrap("click","",(function(e){(m("#tabProgram").hasClass("tabClicked")||m("#tabConfiguration").hasClass("tabClicked")||m("#tabNN").hasClass("tabClicked"))&&m(".scroller-right").clickWrap(),m("#tabNN").clickWrap()}),"tabNN clicked"),m("#menuTabNNLearn").onWrap("click","",(function(e){(m("#tabProgram").hasClass("tabClicked")||m("#tabConfiguration").hasClass("tabClicked")||m("#tabNNlearn").hasClass("tabClicked"))&&m(".scroller-right").clickWrap(),m("#tabNNlearn").clickWrap()}),"tabNNlearn clicked"),m(".navbar-fixed-top").on("mouseleave",(function(e){m(".navbar-fixed-top .dropdown").removeClass("open")})),m(".cancelPopup").onWrap("click",(function(){m(".ui-dialog-titlebar-close").clickWrap()}),"cancel popup clicked"),m("#about-join").onWrap("click",(function(){m("#show-about").modal("hide")}),"hide show about clicked"),m(window).on("beforeunload",(function(e){return p.Msg.POPUP_BEFOREUNLOAD})),m("#navbarCollapse").on("shown.bs.collapse",(function(){var e=Math.min(m(this).height(),Math.max(m("#blocklyDiv").height(),m("#brickly").height(),m("#nn").height()));m(this).css("height",e)})),m(document).onWrap("keydown",(function(e){if("tabProgram"===c.getView()){if((e.metaKey||e.ctrlKey)&&50==e.which){e.preventDefault();var t=c.getBlocklyWorkspace().newBlock("robActions_debug");return t.initSvg(),t.render(),t.setInTask(!1),!1}if((e.metaKey||e.ctrlKey)&&51==e.which){e.preventDefault();var a=c.getBlocklyWorkspace().newBlock("robActions_assert");a.initSvg(),a.setInTask(!1),a.render();var n=c.getBlocklyWorkspace().newBlock("logic_compare");n.initSvg(),n.setMovable(!1),n.setInTask(!1),n.setDeletable(!1),n.render();var r=a.getInput("OUT").connection,i=n.outputConnection;return r.connect(i),!1}var g;if((e.metaKey||e.ctrlKey)&&52==e.which)return e.preventDefault(),(g=c.getBlocklyWorkspace().newBlock("robActions_eval_expr")).initSvg(),g.render(),g.setInTask(!1),!1;if((e.metaKey||e.ctrlKey)&&53==e.which)return e.preventDefault(),(g=c.getBlocklyWorkspace().newBlock("robActions_eval_stmt")).initSvg(),g.render(),g.setInTask(!1),!1;if((e.metaKey||e.ctrlKey)&&83==e.which&&(e.preventDefault(),c.isUserLoggedIn()?"NEPOprog"===c.getProgramName()||e.shiftKey?l.showSaveAsModal():c.isProgramSaved()||l.saveToServer():o.displayMessage("ORA_PROGRAM_GET_ONE_ERROR_NOT_LOGGED_IN","POPUP","")),(e.metaKey||e.ctrlKey)&&82==e.which&&(e.preventDefault(),c.isRunEnabled()&&s.runOnBrick()),(e.metaKey||e.ctrlKey)&&77==e.which&&(e.preventDefault(),c.isUserLoggedIn()?(m("#progList").trigger("Programs"),m("#tabProgList").tabWrapShow()):o.displayMessage("ORA_PROGRAM_GET_ONE_ERROR_NOT_LOGGED_IN","POPUP","")),(e.metaKey||e.ctrlKey)&&73==e.which)return e.preventDefault(),d.importXml(),!1;if((e.metaKey||e.ctrlKey)&&69==e.which)return e.preventDefault(),l.exportXml(),!1}else"tabConfiguration"===c.getView()&&((e.metaKey||e.ctrlKey)&&83==e.which&&(e.preventDefault(),c.isUserLoggedIn()?c.isConfigurationStandard()||e.shiftKey?u.showSaveAsModal():c.isProgramSaved()||u.saveToServer():o.displayMessage("ORA_PROGRAM_GET_ONE_ERROR_NOT_LOGGED_IN","POPUP","")),(e.metaKey||e.ctrlKey)&&77==e.which&&(e.preventDefault(),c.isUserLoggedIn()?m("#tabConfList").tabWrapShow():o.displayMessage("ORA_PROGRAM_GET_ONE_ERROR_NOT_LOGGED_IN","POPUP","")))}))}(),function e(){setTimeout((function(){(v+=1e3)>=c.getPingTime()&&c.doPing()&&(a.ping((function(e){c.setState(e)})),v=0),e()}),1e3)}(),function(){var e=new URL(document.location),t=e.protocol+"//"+e.host,o=e.hash&&e.toString().indexOf("#")<=t.length+1||!1,a=""!==e.search||!1;if(o){var n=!0,i=void 0,s=decodeURI(document.location.hash).split("&&");"#overview"===s[0]?(c.setStartWithoutPopup(),w("ev3lejosv1",(function(){l.newProgram(!0),g.start("overview")})),i=t+P+"tour"+y+"overview"):"#loadProgram"===s[0]&&s.length>=4?(c.setStartWithoutPopup(),w&&w instanceof Function&&w(s[1],d.loadProgramFromXML,[s[2],s[3]]),i=t+P+"loadProgram"+y+"_vC353v-LPr_"):"#loadSystem"===s[0]&&s.length>=2?(c.setStartWithoutPopup(),w&&w instanceof Function&&w(s[1]),i=t+P+"loadSystem"+y+s[1]):"#gallery"===s[0]?(n=!1,i=t+P+"loadSystem"+y+"<ROBOT_SYSTEM>"+S+R):"#tutorial"===s[0]&&(n=!1,i=t+P+"loadSystem"+y+"<ROBOT_SYSTEM>"+S+O,s.length>1&&(i+=y+s[1],s.length>2&&(i+=S+s[2])));var u=void 0;"de"===c.getLanguage()&&n?u="Die eingegebenen URL-Parameter sind in dieser Form veraltet und werden bald nicht mehr unterstützt.\nBitte verwende ab sofort nur noch folgende Schreibweise:\n":"en"===c.getLanguage()&&n?u="The URL parameters entered are outdated in this form and will soon no longer be supported.\nPlease use only the following spelling from now on:":"de"!==c.getLanguage()||n?"en"!==c.getLanguage()||n||(u="The URL parameters entered are outdated in this form and are no longer supported.\nPlease use only the following spelling from now on:"):u="Die eingegebenen URL-Parameter sind in dieser Form veraltet und werden nicht mehr unterstützt.\nBitte verwende ab sofort nur noch folgende Schreibweise:\n",u+=i,alert(u)}if(a){var h=M(W);h&&r.showResetPassword(h);var p=M(C);p&&r.activateAccount(p);var f=M(_);f&&(c.setStartWithoutPopup(),w("ev3lejosv1",(function(){l.newProgram(!0),g.start(f)})));var k=M(L);if(k){c.setStartWithoutPopup();var v=void 0,A={},x=[],B=M(O),D=M(E),U=M(N),K=M(R),G=M(I);if(B)if("true"===B||!0===B)v=function(){m('.navbar-nav a[href="#tutorialList"]').tab("show")};else{var F=M(T);!F||!0!==F&&"true"!==F||c.setKioskMode(!0),v=function(e){b.loadFromTutorial(e)},x.push(B)}else D?(v=d.loadProgramFromXML,x.push("NEPOprog"),x.push(D)):U?v=function(){m("#menuListExamples").clickWrap()}:K?v=function(){m("#tabGalleryList").tabWrapShow()}:G&&G.split(",").forEach((function(e){"calliope2017NoBlue"===k&&"blue"===e||(A[e]=!0)}));w&&w instanceof Function&&w(k,A,v,x)}}}(),f.cleanUri()):alert("Problem")}}));
//# sourceMappingURL=menu.controller.js.map
//# sourceMappingURL=menu.controller.js.map

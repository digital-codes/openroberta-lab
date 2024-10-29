var __assign=this&&this.__assign||function(){return __assign=Object.assign||function(e){for(var t,a=1,r=arguments.length;a<r;a++)for(var l in t=arguments[a])Object.prototype.hasOwnProperty.call(t,l)&&(e[l]=t[l]);return e},__assign.apply(this,arguments)};define(["require","exports","util.roberta","message","guiState.controller","progList.model","program.model","program.controller","blockly","table","jquery","bootstrap-table","bootstrap-tagsinput"],(function(e,t,a,r,l,o,i,n,s,c,g){Object.defineProperty(t,"__esModule",{value:!0}),t.formatLike=t.titleLikes=t.titleNumberOfViews=t.rowAttributes=t.init=t.eventsLike=t.switchLanguage=void 0;var b=["#33B8CA","#EBC300","#005A94","#179C7D","#F29400","#E2001A","#EB6A0A","#8FA402","#BACC1E","#9085BA","#FF69B4","#DF01D7"],d=[];function p(){var e=g('<select id="filterRobot" class="filter form-select"></select>');g("#galleryList .search").prepend(e);var t=a.getRobotGroupsPrettyPrint(),r=g("#filterRobot");for(var l in t)r.append(new Option(t[l],l));r.append(new Option(s.Msg.GALLERY_ALL_ROBOTS,"all",!0,!0));var o=g('<label lkey="Blockly.Msg.GALLERY_SORT_BY">'+s.Msg.GALLERY_SORT_BY+':</label> <select class="filter form-select" id="fieldOrderBy"><option selected="" value="4:desc">'+s.Msg.GALLERY_NEWEST+'</option><option value="4:asc">'+s.Msg.GALLERY_OLDEST+'</option><option value="1:asc">'+s.Msg.GALLERY_PROGRAM_NAME+'</option><option value="0:asc">'+s.Msg.GALLERY_ROBOT+"</option></select>");g("#galleryList .search").append(o)}function u(){var e=l.getLanguage(),r={columns:[{sortable:!0,title:"",formatter:c.CardView.robot},{title:"",sortable:!0,formatter:c.CardView.name},{title:"",sortable:!0,formatter:c.CardView.programDescription},{title:"",sortable:!0,formatter:function(e){return c.CardView.titleLabel(e,"GALLERY_BY","cardViewInfo")}},{sortable:!0,title:"",formatter:function(e){return c.CardView.titleLabel(a.formatDate(e.replace(/\s/,"T")),"GALLERY_DATE","cardViewInfo")}},{title:"",formatter:function(e){return c.CardView.titleTypcn(e,"eye-outline")},sortable:!0},{title:"",formatter:function(e){return c.CardView.titleTypcn(e,"heart-full-outline")},sortable:!0},{title:"",sortable:!0,formatter:function(e,t){return c.CardView.programTags(t[2])}},{title:"",events:t.eventsLike,formatter:t.formatLike}],filterControl:"true",locale:e,rowAttributes:T,toolbar:"#galleryListToolbar",sortName:4,sortOrder:"desc",height:a.calcDataTableHeight()},o=__assign(__assign(__assign({},c.CommonTable.options),c.CardView.options),r);g("#galleryTable").bootstrapTable(o)}function f(){g("#galleryTable").bootstrapTable("resetView",{height:a.calcDataTableHeight()}),g("#galleryList .fixed-table-container.fixed-height").addClass("has-card-view"),g(".infoTags").tagsinput(),g("#galleryTable .bootstrap-tagsinput").addClass("galleryTags"),g("#galleryList").find(".galleryTags>input").attr("readonly","true"),g("#galleryList").find("span[data-role=remove]").addClass("hidden")}function L(){g("#galleryTable").bootstrapTable("showLoading");var e={},t=g("#filterRobot").val();"all"!==t&&(e.group=t),o.loadGalleryList(y,e)}function y(e){a.response(e),"ok"===e.rc&&(d=e.programNames,g("#galleryTable").bootstrapTable("load",e.programNames)),g("#galleryTable").bootstrapTable("hideLoading"),f()}function m(e,t,a){var r=d.indexOf(a);a[6]+=e,a[8]=e>0,g("#galleryTable").bootstrapTable("updateRow",{index:r,row:a}),f()}function k(){g("#galleryTable").onWrap("search.bs.table",(function(){f()}),"Load program from gallery double clicked"),g("#galleryTable").onWrap("click-row.bs.table",(function(e,t){n.loadFromGallery(t)}),"Load program from gallery double clicked"),g("#galleryList").find('button[name="refresh"]').onWrap("click",(function(){return L(),!1}),"refresh gallery list clicked"),g("#filterRobot").onWrap("change",L,"gallery filter changed"),g("#fieldOrderBy").onWrap("change",(function(e){var t=e.target.value.split(":"),a=parseInt(t[0]);g("#galleryTable").bootstrapTable("sortBy",{field:a,sortOrder:t[1]}),f()})),g("#filterRobot").val(l.getRobotGroup()),L()}t.init=function(){u(),p(),g("#tabGalleryList").onWrap("shown.bs.tab",(function(e){return guiStateController.setView("tabGalleryList"),k(),!1}),"gallery clicked"),g("#backGalleryList").onWrap("click",(function(){return g("#"+l.getPrevView()).tabWrapShow(),!1}),"back to program view"),g(window).onWrap("resize",(function(){return f()})),g("#galleryTable").onWrap("post-body.bs.table",(function(){f()}))},t.switchLanguage=function(){g("#galleryTable").bootstrapTable("destroy"),u(),p(),k()},t.eventsLike={"click .like":function(e,t,a,l){if(e.stopPropagation(),1!=g(e.target).data("blocked"))return g(e.target).data("blocked",1),i.likeProgram(!0,a[1],a[3],a[0],(function(t){"ok"==t.rc?(m(1,0,a),g(e.target).data("blocked",0)):g(e.target).data("blocked",0),r.displayInformation(t,t.message,t.message,a[1])})),!1},"click .dislike":function(e,t,a,l){if(e.stopPropagation(),1!=g(e.target).data("blocked"))return g(e.target).data("blocked",1),i.likeProgram(!1,a[1],a[3],a[0],(function(t){"ok"==t.rc?(m(-1,0,a),g(e.target).data("blocked",0)):g(e.target).data("blocked",0),r.displayInformation(t,t.message,t.message,a[1])})),!1}};var T=function(e,t){var r=a.getHashFrom(e[0]+e[1]+e[3]);return{style:"background-color :"+b[r%b.length]+";cursor: pointer;  z-index: 1;"}};t.rowAttributes=T;t.titleNumberOfViews='<span class="galleryIcon typcn typcn-eye-outline" />';t.titleLikes='<span class="galleryIcon typcn typcn-heart-full-outline" />';t.formatLike=function(e,t,a){return l.isUserLoggedIn()?e?'<div class="galleryLike"><button href="#" class="dislike galleryLike btn"><span lkey="Blockly.Msg.GALLERY_DISLIKE">'+(s.Msg.GALLERY_DISLIKE||"GALLERY_DISLIKE")+"</span></button></div>":'<div class="galleryLike"><button href="#" class="like galleryLike btn"><span lkey="Blockly.Msg.GALLERY_LIKE">'+(s.Msg.GALLERY_LIKE||"GALLERY_LIKE")+"</span></button></div>":'<div style="display:none;" />'}}));
//# sourceMappingURL=galleryList.controller.js.map
//# sourceMappingURL=galleryList.controller.js.map

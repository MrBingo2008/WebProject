function eXcell_link(a){this.cell=a;this.grid=this.cell.parentNode.grid;this.isDisabled=function(){return!0};this.edit=function(){};this.getValue=function(){if(this.cell.firstChild.getAttribute){var a=this.cell.firstChild.getAttribute("target");return this.cell.firstChild.innerHTML+"^"+this.cell.firstChild.getAttribute("href")+(a?"^"+a:"")}else return ""};this.setValue=function(a){if(typeof a!="number"&&(!a||a.toString().PA()==""))return this.dq("&nbsp;",b),this.cell.mG= !0;var b=a.split("^");b.length==1?b[1]="":b.length>1&&(b[1]="href='"+b[1]+"'",b[1]+=b.length==3?" target='"+b[2]+"'":" target='_blank'");this.dq("<a "+b[1]+" onclick='(_isIE?event:arguments[0]).cancelBubble = true;'>"+b[0]+"</a>",b)}}eXcell_link.prototype=new gD;eXcell_link.prototype.getTitle=function(){var a=this.cell.firstChild;return a&&a.tagName?a.getAttribute("href"):""};eXcell_link.prototype.getContent=function(){var a=this.cell.firstChild;return a&&a.tagName?a.innerHTML:""};
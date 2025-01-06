function BBAsyncImageLoader(){
	this.bloburl = ""
}
BBAsyncImageLoader.prototype.DownloadImage = function (url) {
    this.bloburl=""
    var xhr = new XMLHttpRequest();
    xhr.open("GET", url, true);  
    xhr.responseType = "blob";
    xhr.onreadystatechange = () => {  
        if (xhr.readyState === 4 && xhr.status === 200) {
            var blobUrl = URL.createObjectURL(xhr.response);
            var img = new Image();
            img.src = blobUrl;
            img.onload = () => {  
                this.bloburl = blobUrl
            };
        }
    };
    xhr.send();    
};
BBAsyncImageLoader.prototype.isDoneDownload = function () {
    if (this.bloburl == "") {
        return ""
    }else{
		return this.bloburl
	}
};
BBAsyncImageLoader.prototype.clearDownload = function () {
	this.bloburl=""
};

var cropper;
var uploadURL = "https://eny8hx27g8gw.x.pipedream.net/";
var options = {
	preview: '.imgPreview'
};
var image = document.getElementById('precoScannerImage');

document.getElementById('inputImage').onchange = function () {
	var file;
	var files = this.files;

	if (files && files.length) {
		file = files[0];

		if (/^image\/\w+/.test(file.type)) {
            uploadedImageType = file.type;
            uploadedImageName = file.name;

            image.src = uploadedImageURL = URL.createObjectURL(file);

            init();

            inputImage.value = null;
		} else {
		  window.alert('Please choose an image file.');
		}
	}
};
document.getElementById('buttonScanner').onclick = function (){
    var base64 = $.ajax({
        url:"http://localho.st:1235/getBase64",
        crossDomain: true,
        complete: function (data) {
            image.src = "data:image/png;base64, " + data.responseText;
            init();
        }
    });
};
document.getElementById('buttonSave').onclick = function (){
    canvas = cropper["getCroppedCanvas"]();
    $.ajax({url:uploadURL, crossdomain: true,
            type: "POST", data: {image: canvas.toDataURL() }});
};
document.getElementById('buttonMove').onclick = function (){
     if (!cropper)
        cropper = new Cropper(image, options);
    cropper["setDragMode"]("move");
};
document.getElementById('buttonCrop').onclick = function (){
     if (!cropper)
        cropper = new Cropper(image, options);
    cropper["setDragMode"]("crop");
};
document.getElementById('buttonZoomIn').onclick = function (){
     if (!cropper)
        cropper = new Cropper(image, options);
    cropper["zoom"](0.1);
};
document.getElementById('buttonZoomOut').onclick = function (){
     if (!cropper)
        cropper = new Cropper(image, options);
    cropper["zoom"](-0.1);
};
function init() {
      if (cropper) {
        cropper.destroy();
      }

      cropper = new Cropper(image, options);
}

window.onload = function() {
  init();
};

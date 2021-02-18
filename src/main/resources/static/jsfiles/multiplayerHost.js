function displayImageLibrary(event) {
    if ($(event.target).parent(".image-wrapper-open").length !== 0) { // event. target = where i clicked
        let imageWrapper = $(event.target).parent(".image-wrapper-open");
        if(!imageWrapper.hasClass("image-wrapper-sent")) {
            let isSelected = imageWrapper.hasClass("image-wrapper-selected");
            $(".widget-wrapper").find(".image-wrapper-selected").removeClass("image-wrapper-selected");
            if(!isSelected) {
                imageWrapper.addClass("image-wrapper-selected");
                let id = $(event.target).attr("id");
                $("#partImageId").val(id);
            }
        }

        if ($(".widget-wrapper").find(".image-wrapper-selected").length === 0)
            $("#sendButton").prop('disabled', true);
        else {
            $("#sendButton").prop('disabled', false);
        }

    } else {
        let imageDiv = $("#imageDiv");
        if (!imageDiv.hasClass("image-div-open")) {
            imageDiv.children(".image-wrapper").each(function (i, e) { // i = index , e = element
                let translateValue = i * 25;
                let tmp = $(e);
                tmp.css("transform", "translate(" + translateValue + "%) perspective(500px) rotate3d(0, 1, 0, 45deg) scale(0.4)");
                tmp.addClass("image-wrapper-open");
                tmp.removeClass("image-wrapper-closed")
            });
        } else {
            imageDiv.children(".image-wrapper").each(function (i, e) {
                let translateValue = i * 100;
                let tmp = $(e);
                tmp.css("transform", "");
                tmp.removeClass("image-wrapper-open");
                tmp.addClass("image-wrapper-closed")

            });
        }
        $(imageDiv).toggleClass("image-div-open");
    }

}
//# = id; $ --jQuery function ; $( = will make a jq object with the tag in the brackets
$("#imageDiv").on("click", function (event) {
    displayImageLibrary(event)
});
// . = class. on = adds an even(in this case "click")
$("#cancelButton").on("click", function (event) {
    $(".widget-wrapper").find(".image-wrapper-selected").removeClass("image-wrapper-selected");
    $("#sendButton").prop('disabled', true); //disables the this button if nothing is selected; prop = propriety (id, class, disabled...)
    $("#partImageId").val("-10");
});
// The function "find" will find all widget-wrapper objects and apply the find function

$("#sendButton").on("click", function (event) {
    $(".widget-wrapper").find(".image-wrapper-selected").addClass("image-wrapper-sent").removeClass("image-wrapper-selected");
});

function arrowLeftRight(event) {
    let ImageSelectedBefore = $(".widget-wrapper").find(".image-wrapper-selected");
    let ImageSelectedNow = null;
    if (ImageSelectedBefore.length === 0) {
        ImageSelectedNow = $(".image-wrapper").first();
        if (!ImageSelectedNow.hasClass("image-wrapper-sent")){
            ImageSelectedNow.addClass("image-wrapper-selected");
        }
        else{
            ImageSelectedNow = keepGoingRight(ImageSelectedNow);
            ImageSelectedNow.addClass("image-wrapper-selected");
        }
        add_imageId(ImageSelectedNow[0].firstElementChild);
    }
    else {
        switch (event.keyCode) {
            case 37:
                ImageSelectedNow = keepGoingLeft(ImageSelectedBefore);
                if (ImageSelectedNow == null) {
                    ImageSelectedNow = ImageSelectedBefore;
                } else {
                    ImageSelectedBefore.removeClass("image-wrapper-selected");
                    ImageSelectedNow.addClass("image-wrapper-selected");
                }
                add_imageId(ImageSelectedNow[0].firstElementChild);
                break;
            case 39:
                ImageSelectedNow = keepGoingRight(ImageSelectedBefore);
                if (ImageSelectedNow == null) {
                    ImageSelectedNow = ImageSelectedBefore;
                } else {
                    ImageSelectedBefore.removeClass("image-wrapper-selected");
                    ImageSelectedNow.addClass("image-wrapper-selected");
                }
                add_imageId(ImageSelectedNow[0].firstElementChild);
                break;

        }
    }
}


function add_imageId(element){
    let id = $(element).attr("id");
    $("#partImageId").val(id);
}

function keepGoingRight(element){
    let newElement = element.next();
    if (newElement.hasClass("image-wrapper-sent")){
        return keepGoingRight(newElement)
    }else{
        return newElement;
    }
}
function keepGoingLeft(element) {
    let newElement = element.prev();
    if (newElement.hasClass("image-wrapper-sent")) {
        return keepGoingLeft(newElement)
    } else {
        return newElement;
    }
}

document.onkeydown = function(event) {
    if(event.keyCode == 39 || event.keyCode == 37){
        arrowLeftRight(event);

        if ($(".widget-wrapper").find(".image-wrapper-selected").length === 0)
            $("#sendButton").prop('disabled', true);
        else {
            $("#sendButton").prop('disabled', false);
        }
    }
}
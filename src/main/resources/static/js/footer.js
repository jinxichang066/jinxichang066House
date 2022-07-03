
$.ajax({
    type: "post",
    processData: false,
    contentType: false,
    url: apiPath + '/system/load',
    data: {},
    success: function (data) {
        if (data.code === 2000) {
            let system = data.data;

            document.getElementById("footerDiv").innerText = system.copyright;
        }
    }
});
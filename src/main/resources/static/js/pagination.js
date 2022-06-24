// 2022-06-19 不要中间页面，只保留上一页、下一页

function initPagination() {
    let paginationUlEvent = document.getElementById("pagination-ul");
    paginationUlEvent.innerHTML = '';
    let leftButton = document.createElement("button");
    let rightButton = document.createElement("button");
    let leftIcon = document.createElement("i");
    let rightIcon = document.createElement("i");
    leftButton.style.cssText = "width: 20px; height: 20px";
    rightButton.style.cssText = "width: 20px; height: 20px";
    leftIcon.style.cssText = "width: 20px; height: 20px";
    rightIcon.style.cssText = "width: 20px; height: 20px";
    let clickIndex = 0;

    leftButton.id = "pagination-btn-left";
    leftButton.style.cursor = "not-allowed";
    leftButton.title = "上一页";
    leftButton.appendChild(leftIcon);
    paginationUlEvent.appendChild(leftButton);

    let totalPageNum = pageTotal();

    rightButton.id = "pagination-btn-right";
    rightButton.style.cursor = "pointer";
    rightButton.title = "下一页";
    rightButton.appendChild(rightIcon);
    paginationUlEvent.appendChild(rightButton);

    function pageTotal() {
        if (pageSize !== 0 && totalCount % pageSize === 0) {
            return totalCount / pageSize
        } else if (pageSize !== 0 && totalCount % pageSize !== 0) {
            return Math.ceil(totalCount / pageSize);
        } else return 0;
    }


    function clickButtonEvent(i) {
        //展示上一页/下一页数据
        playPagination(i + 1);
        console.log("pageNum:" + (i + 1));

        clickIndex = i;
        switch (i) {
            case 0: {
                leftButton.style.cursor = "not-allowed";
                rightButton.style.cursor = "pointer";
                leftButton.className = "noSelect";
                rightButton.className = "isSelect";
            }
                break;
            case (totalPageNum - 1): {
                leftButton.style.cursor = "pointer";
                rightButton.style.cursor = "not-allowed";
                leftButton.className = "isSelect";
                rightButton.className = "noSelect";
            }
                break;
            default: {
                leftButton.style.cursor = "pointer";
                rightButton.style.cursor = "pointer";
                leftButton.className = "isSelect";
                rightButton.className = "isSelect";
            }
        }
    }

    leftButton.onclick = function () {
        console.log(leftButton.style)
        if (leftButton.style.cursor === "not-allowed") {
            return false;
        } else if (leftButton.style.cursor === "pointer") {
            if (clickIndex <= 0) {
                return false;
            } else {
                let index = clickIndex - 1;
                clickButtonEvent(index);
            }
        }
    }

    rightButton.onclick = function () {
        if (rightButton.style.cursor === "not-allowed") {
            return false;
        } else if (rightButton.style.cursor === "pointer") {
            if (clickIndex >= totalPageNum - 1) {
                return false;
            } else {
                let index = clickIndex + 1;
                clickButtonEvent(index);
            }
        }
    }

}
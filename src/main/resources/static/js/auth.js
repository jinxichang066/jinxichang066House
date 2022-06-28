var tokenKey = "gasToken";

function doAuth(res) {
    if (res.code === 2000) {
        return true;
    }
    if (res.code === 401) {
        localStorage.removeItem(tokenKey);
        window.location.href = "login.html";
    }
}

// 将token存入/更新到ls
function setGasToken(data) {
    localStorage.setItem(tokenKey, JSON.stringify(data));
}

// 获取ls中的token
function getGasToken() {
    let token;
    let tokenJson = JSON.parse(localStorage.getItem(tokenKey));
    if (tokenJson != null) {
        token = tokenJson.id;
    }
    console.log("gasToken:" + token);

    if (token === undefined || token === null || token === "") {
        token = "";
        window.location.href = "login.html";
    }
    return token;
}

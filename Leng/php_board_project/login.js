function login_check() {
    var xmlReqObj = new XMLHttpRequest();
    xmlReqObj.onreadystatechange = function () {
        if (xmlReqObj.readyState == 4 && xmlReqObj.status == 200) {
            var result = xmlReqObj.responseText;
            //alert(result);
            //로그인 조건이 불충족시
            if(result == "no"){
                var log_div = document.getElementById("login_div");
                log_div.innerHTML="";
                while(log_div.children.length>0){
                    log_div.removeChild(log_div.lastChild);
                }

                var create_id = document.createElement("input");
                create_id.setAttribute("type","text");
                create_id.setAttribute("class","form-control");
                create_id.setAttribute("placeholder","name");
                create_id.setAttribute("name","create_id");
                log_div.appendChild(create_id);

                var create_pwd = document.createElement("input");
                create_pwd.setAttribute("type","password");
                create_pwd.setAttribute("class","form-control");
                create_pwd.setAttribute("placeholder","password");
                create_pwd.setAttribute("name","create_pwd");
                log_div.appendChild(create_pwd);

                var submit_button = document.createElement("input");
                submit_button.setAttribute("type","button");
                submit_button.setAttribute("class","btn btn-sm btn-success");
                submit_button.setAttribute("value","로그인");
                submit_button.setAttribute("id","btn1");
                submit_button.setAttribute("onclick","login_click()");
                log_div.appendChild(submit_button);

                var submit_button = document.createElement("input");
                submit_button.setAttribute("type","button");
                submit_button.setAttribute("value","회원가입");
                submit_button.setAttribute("class","btn btn-sm btn-primary");
                submit_button.setAttribute("id","btn2");
                submit_button.setAttribute("onclick","location.href='sign_up.html'");
                log_div.appendChild(submit_button);
            }
            else{
                //로그인이 되어 있거나 로그인 버튼을 눌렀을 시 있는 계정일경우
                var result = xmlReqObj.responseText;
                var log_div = document.getElementById("login_div");
                log_div.setAttribute("class","bg-success");
                while(log_div.children.length>0){
                    log_div.removeChild(log_div.lastChild);
                }
                log_div.innerHTML=result+"님 환영합니다";

                var submit_button = document.createElement("input");
                submit_button.setAttribute("type","button");
                submit_button.setAttribute("value","로그아웃");
                submit_button.setAttribute("class","btn btn-sm btn-primary");
                submit_button.setAttribute("id","btn1");
                submit_button.setAttribute("onclick","logout_click()");
                log_div.appendChild(submit_button);
            }
        }
    }
    url = "DB_login.php";
    xmlReqObj.open("GET", url, true);
    xmlReqObj.send();
}

//로그아웃 버튼 클릭시
function logout_click(){
    var xmlReqObj = new XMLHttpRequest();
    xmlReqObj.onreadystatechange = function () {
        if (xmlReqObj.readyState == 4 && xmlReqObj.status == 200) {
            login_check(1);
            window.location.reload(true);
        }
    }
    url = "DB_login.php?logout="+1;
    xmlReqObj.open("GET", url, true);
    xmlReqObj.send();
}

//로그인 버튼 클릭시
function login_click(temp){
    var xmlReqObj = new XMLHttpRequest();
    xmlReqObj.onreadystatechange = function () {
        if (xmlReqObj.readyState == 4 && xmlReqObj.status == 200) {
            login_check();
            window.location.reload(true);
        }
    }
    //로그아웃 버튼 클릭시
    if(temp == 1){
        url = "DB_login.php";
    }
    else {
        url = "DB_login.php?create_id=" + document.getElementsByName("create_id")[0].value +
            "&create_pwd=" + document.getElementsByName("create_pwd")[0].value;
    }
    xmlReqObj.open("GET", url, true);
    xmlReqObj.send();
}
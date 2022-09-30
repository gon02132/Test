<?php
//로그인 중일 때,
if($login_checked != 0){
    echo "<div class='bg-success'> $login_checked 님 환영합니다 </div>";
    echo "<input type='button' value='로그아웃' class='btn btn-sm btn-primary' id='logout' onclick='logout()'>";
}
//로그인 중이지 않을 때,
else{
    echo "<input type='text' class='form-control' placeholder='name' name='user_id'>";
    echo "<input type='password' class='form-control' placeholder='password' name='user_pw'>";
    echo "<input type='button' value='로그인' class='btn btn-sm btn-success' id='submit' onclick='login()'>";
    echo "<input type='button' value='회원가입' class='btn btn-sm btn-primary' id='crt_user'>";
}
?>

<script>
    //로그인 버튼 클릭시,
    function login() {
        var xmlReqObj = new XMLHttpRequest();
        xmlReqObj.onreadystatechange = function () {
            if (xmlReqObj.readyState == 4 && xmlReqObj.status == 200) {
                var login_div = document.getElementById("login_div");
                if (xmlReqObj.responseText != "") {
                    //서버로부터 유저닉네임을 받아온다
                    //로그인 부분을 전부 지운다
                    while (login_div.children.length > 0) {
                        login_div.removeChild(login_div.lastChild);
                    }
                    //닉네임 출력
                    var create_user_name = document.createElement("div");
                    create_user_name.setAttribute("class", "bg-success");
                    create_user_name.innerHTML = xmlReqObj.responseText + " 님 환영합니다";
                    login_div.appendChild(create_user_name);

                    //로그아웃 버튼 출력
                    var submit_button = document.createElement("input");
                    submit_button.setAttribute("type", "button");
                    submit_button.setAttribute("value", "로그아웃");
                    submit_button.setAttribute("class", "btn btn-sm btn-primary");
                    submit_button.setAttribute("id", "logout");
                    submit_button.setAttribute("onclick", "logout()");
                    login_div.appendChild(submit_button);

                    //글쓰기 버튼 생성
                    var create_write_button = document.createElement("input");
                    create_write_button.setAttribute("type", "button");
                    create_write_button.setAttribute("onclick", "location.href=\"write.html\"");
                    create_write_button.setAttribute("class", "btn btn-sm btn-info");
                    create_write_button.setAttribute("value", "글쓰기");
                    document.getElementById("write_div").appendChild(create_write_button);


                }else{
                    //ID나 pw가 틀렷을시,
                    login_div.removeChild(login_div.lastChild);

                    var create_user_name = document.createElement("div");
                    create_user_name.setAttribute("class", "bg-success");
                    create_user_name.innerHTML = "ID혹은 PW를 <br>다시 확인하세요";
                    login_div.appendChild(create_user_name);
                }
            }
        }
        url = "index.php";
        xmlReqObj.open("POST", url, true);
        xmlReqObj.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
        xmlReqObj.send("user_id="+document.getElementsByName("user_id")[0].value+
                       "&user_pw="+document.getElementsByName("user_pw")[0].value);
    }

    //로그아웃 버튼 클릭시,
    function logout() {
        //로그인 div 자식 삭제
        var login_div = document.getElementById("login_div");
        while (login_div.children.length > 0) {
            login_div.removeChild(login_div.lastChild);
        }
        //글쓰기 div 자식 삭제
        var write_div = document.getElementById("write_div");
        while (write_div.children.length > 0) {
            write_div.removeChild(write_div.lastChild);
        }
        var submit_button = document.createElement("input");
        submit_button.setAttribute("type", "id");
        submit_button.setAttribute("class", "form-control");
        submit_button.setAttribute("placeholder", "name");
        submit_button.setAttribute("name", "user_id");
        login_div.appendChild(submit_button);

        var submit_button = document.createElement("input");
        submit_button.setAttribute("type", "password");
        submit_button.setAttribute("class", "form-control");
        submit_button.setAttribute("placeholder", "password");
        submit_button.setAttribute("name", "user_pw");
        login_div.appendChild(submit_button);

        var submit_button = document.createElement("input");
        submit_button.setAttribute("type", "button");
        submit_button.setAttribute("value", "로그인");
        submit_button.setAttribute("class", "btn btn-sm btn-success");
        submit_button.setAttribute("id", "submit");
        submit_button.setAttribute("onclick","login()");
        login_div.appendChild(submit_button);

        var submit_button = document.createElement("input");
        submit_button.setAttribute("type", "button");
        submit_button.setAttribute("value", "회원가입");
        submit_button.setAttribute("class", "btn btn-sm btn-primary");
        submit_button.setAttribute("id", "crt_user");
        submit_button.setAttribute("onclick","");
        login_div.appendChild(submit_button);


        var xmlReqObj = new XMLHttpRequest();
        xmlReqObj.onreadystatechange = function () {
            if (xmlReqObj.readyState == 4 && xmlReqObj.status == 200) {
            }
        }
        url = "index.php";
        xmlReqObj.open("POST", url, true);
        xmlReqObj.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
        xmlReqObj.send("logout_click="+1);
    }

</script>

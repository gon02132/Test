<?php
@session_start();
@include "databases_info.php";
$table_name = "sign_up_board";
//로그아웃을 눌렀다면?
if(isset($_GET['logout'])){
    session_unset();
    session_destroy();
    echo "no";
}
//로그인 중이라면?
else if(isset($_SESSION["name_session"])){
    echo $_SESSION["name_session"];
}
//로그인을 눌렀다면?
else {
    if (isset($_GET['create_id']) && isset($_GET['create_pwd'])) {
        $user_id = $_GET['create_id'];
        $user_pwd = $_GET['create_pwd'];
        if ($user_id == "" || $user_pwd == "") {
            echo "아이디랑 비밀번호 둘다 입력해라";
        } else {
            //특수문자(띄워쓰기 포함) 처리
            $user_id = htmlspecialchars($user_id);
            $user_pwd = htmlspecialchars($user_pwd);

//공백(space bar) 처리
            $user_id = nl2br(str_replace(" ", '&nbsp;', $user_id));
            $user_pwd = nl2br(str_replace(" ", '&nbsp;', $user_pwd));

//특수문자 처리
            $user_id = str_replace("\\", "\\\\", $user_id);
            $user_pwd = str_replace("\\", "\\\\", $user_pwd);

            //db 접속
            @$db_con = mysql_connect(HOST, USER, PASSWORD);
            if ($db_con) {
                if (mysql_select_db(DB_NAME, $db_con)) {
                    $query = "SELECT user_name from " . $table_name . " WHERE user_id='" . $user_id .
                        "' AND user_password='" . $user_pwd . "'";
                    if ($result = mysql_query($query)) {
                        if (mysql_num_rows($result) != 1) {
                            echo "no";
                        } else {
                            $result_arr = mysql_fetch_row($result);
                            $_SESSION["id_session"] = $user_id;
                            $_SESSION["password_session"] = $user_pwd;
                            $_SESSION["name_session"] = $result_arr[0];
                            echo $_SESSION["name_session"];

                        }
                    } else
                        echo mysql_error($db_con);
                } else {
                    echo "DB접속 실패";
                }
            } else {
                echo "mysql접속 실패";
            }
        }
    } else {
        echo "no";
    }
}
?>
<?php
@session_start();
//DB 정보 include
@include "databases_info.php";
$table_name = "sign_up_board";
//임시
if(!isset($_POST['user_id']) || !isset($_POST['user_pwd']) ||!isset($_POST['user_name'])){
    echo("<script>location.href='sign_up.html';</script>");
}
else {
    $user_id = $_POST['user_id'];
    $user_pwd = $_POST['user_pwd'];
    $user_name = $_POST['user_name'];

//현재 날짜
    $now_date = date('Y-m-d H:i:s', time());
//특수문자(띄워쓰기 포함) 처리
    $user_id = htmlspecialchars($user_id);
    $user_pwd = htmlspecialchars($user_pwd);
    $user_name = htmlspecialchars($user_name);

//공백(space bar) 처리
    $user_id = nl2br(str_replace(" ", '&nbsp;', $user_id));
    $user_pwd = nl2br(str_replace(" ", '&nbsp;', $user_pwd));
    $user_name = nl2br(str_replace(" ", '&nbsp;', $user_name));

//특수문자 처리
    $user_id = str_replace("\\", "\\\\", $user_id);
    $user_pwd = str_replace("\\", "\\\\", $user_pwd);
    $user_name = str_replace("\\", "\\\\", $user_name);

//중복 확인
    @$db_con = mysql_connect(HOST, USER, PASSWORD);
    if ($db_con) {
        if (mysql_select_db(DB_NAME, $db_con)) {
            $query = "SELECT * from " . $table_name . " WHERE user_id='" . $user_id . "'";
            if ($result = mysql_query($query)) {
                if (mysql_num_rows($result) >= 1) {
                    $query = 1;
                }
            } else
                echo mysql_error($db_con);
            if($query != 1) {
                $query = "SELECT * from " . $table_name . " WHERE user_name='" . $user_name . "'";
                if ($result = mysql_query($query)) {
                    if (mysql_num_rows($result) >= 1) {
                        $query = 2;
                    }
                } else
                    echo mysql_error($db_con);
            }
        } else {
            echo "DB접속 실패";
        }
        mysql_close($db_con);
    } else {
        echo "mysql접속 실패";
    }
    if ($query == 1) {
        echo("<script>alert('이미 있는 ID입니다'); location.href='sign_up.html';</script>");
    }
    else if($query == 2){
        echo("<script>alert('이미 있는 NAME입니다'); location.href='sign_up.html';</script>");
    }
    else {
//db 접속 -> 중복검사 끝
        @$db_con = mysql_connect(HOST, USER, PASSWORD);
        if ($db_con) {
            if (mysql_select_db(DB_NAME, $db_con)) {
                $query = "INSERT into " . $table_name . "(user_id,user_password,user_name,date)
                   values('$user_id','$user_pwd','$user_name','$now_date')";
                if ($result = mysql_query($query)) {
                    echo("<script>location.href='list.html';</script>");
                } else
                    echo mysql_error($db_con);
            } else {
                echo "DB접속 실패";
            }
            mysql_close($db_con);
        } else {
            echo "mysql접속 실패";
        }
    }
}
?>
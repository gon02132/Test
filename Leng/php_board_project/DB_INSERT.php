<?php
@session_start();
//DB 정보 include
@include "databases_info.php";

//임시
if(isset($_SESSION["id_session"]) && isset($_SESSION["name_session"])){
    $user_id=$_SESSION["id_session"];
    $user_name =$_SESSION["name_session"];
}
else {
    $user_id = "112233";
    $user_name = "cyka";
}
//현재 날짜
$now_date = date('Y-m-d H:i:s', time());
//제목
$title = $_POST['title'];

//제목 미입력시 재입력 받는다
$text_area = $_POST['text_main'];
if($title==null){
    echo("<script>location.href='write.html';</script>");
}

else {
//특수문자(띄워쓰기 포함) 처리
    $title = htmlspecialchars($title);
    $text_area = htmlspecialchars($text_area);

//공백(space bar) 처리
    $title = nl2br(str_replace(" ", '&nbsp;', $title));
    $text_area = nl2br(str_replace(" ", '&nbsp;', $text_area));

//특수문자 처리
    $title = str_replace("\\","\\\\",$title);
    $text_area = str_replace("\\","\\\\",$text_area);

//db 접속
    @$db_con = mysql_connect(HOST, USER, PASSWORD);
    if ($db_con) {
        if (mysql_select_db(DB_NAME, $db_con)) {
            $query = "INSERT into " . TABLE_NAME . "(title,user_name,date,user_id,textarea)
                   values('$title','$user_name','$now_date','$user_id','$text_area')";
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
?>
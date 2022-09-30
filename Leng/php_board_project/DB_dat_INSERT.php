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
//게시글번호와 댓글텍스트
$text_area=$_POST['text_area'];
$text_num=$_POST['text_num'];

//특수문자(띄워쓰기 포함) 처리
$text_area = htmlspecialchars($text_area);

//공백(space bar) 처리
$text_area = nl2br(str_replace(" ", '&nbsp;', $text_area));

//특수문자 처리
$text_area = str_replace("\\","\\\\",$text_area);

//db 접속
@$db_con = mysql_connect(HOST, USER, PASSWORD);
if ($db_con) {
    if (mysql_select_db(DB_NAME, $db_con)) {
        //댓글의 대한 댓글
        if(isset($_POST['dat_num'])){
            echo "쑤카!";
            $dat_num=$_POST['dat_num'];
            $query = "INSERT into " . TABLE_NAME . "(board_reid,rereid,title,user_name,date,user_id,textarea)
                   values($text_num,$dat_num,'none','$user_name','$now_date','$user_id','$text_area')";
        }
        //댓글
        else {
            echo "안쑤카!";
            $query = "INSERT into " . TABLE_NAME . "(board_reid,title,user_name,date,user_id,textarea)
                   values($text_num,'none','$user_name','$now_date','$user_id','$text_area')";
        }
        if ($result = mysql_query($query)) {
            echo "성공";
        } else
            echo mysql_error($db_con);
    } else {
        echo "DB접속 실패";
    }
    mysql_close($db_con);
} else {
    echo "mysql접속 실패";
}
?>
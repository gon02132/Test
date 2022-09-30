<?php
//DB 정보 include
@session_start();
@include "databases_info.php";
//제목 수정과 내용 수정
$title_area = $_POST['title_area_name'];
$text_area = $_POST['text_area_name'];

$board_id = $_POST['see_cyka'];
$now_date = date('Y-m-d H:i:s', time());

if(isset($_SESSION["id_session"]) && isset($_SESSION["name_session"])){
    $user_id=$_SESSION["id_session"];
    $user_name =$_SESSION["name_session"];
}
else {
    $user_id = "112233";
    $user_name = "cyka";
}

//특수문자(띄워쓰기 포함) 처리
$title_area = htmlspecialchars($title_area);
$text_area = htmlspecialchars($text_area);
//공백(space bar) 처리
$title_area = nl2br(str_replace(" ", '&nbsp;', $title_area));
$text_area = nl2br(str_replace(" ", '&nbsp;', $text_area));
//특수문자 처리
$title_area = str_replace("\\","\\\\",$title_area);
$text_area = str_replace("\\","\\\\",$text_area);

//db 접속 -> 내용이랑 날짜 업데이트
@$db_con = mysql_connect(HOST, USER, PASSWORD);
if ($db_con) {
    if (mysql_select_db(DB_NAME, $db_con)) {
        $query = "UPDATE ".TABLE_NAME." SET "."title="."'$title_area'"." WHERE board_id=".$board_id;
        if ($result = mysql_query($query)) {
        } else
            echo mysql_error($db_con);

        $query = "UPDATE ".TABLE_NAME." SET "."date="."'$now_date'"." WHERE board_id=".$board_id;
        if ($result = mysql_query($query)) {
        } else
            echo mysql_error($db_con);

        $query = "UPDATE ".TABLE_NAME." SET "."textarea="."'$text_area'"." WHERE board_id=".$board_id;
        if ($result = mysql_query($query)) {
        } else
            echo mysql_error($db_con);

    } else {
        echo "DB접속 실패";
    }
} else {
    echo "mysql접속 실패";
}
//자동창닫기
/*
echo "<script> 
self.close();
</script>";
*/

?>
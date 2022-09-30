<?php
@session_start();
//DB 정보 include
@include "databases_info.php";
//임시
$user_id = "112233";
$user_name = "cyka";
$board_id = $_GET['temp'];

//db 접속
@$db_con = mysql_connect(HOST, USER, PASSWORD);
if ($db_con) {
    if (mysql_select_db(DB_NAME, $db_con)) {
        $query = "delete from ".TABLE_NAME." where board_id=".$board_id.
            " OR board_reid=".$board_id." OR rereid=".$board_id;
        if ($result = mysql_query($query)) {
        } else
            echo mysql_error($db_con);
    } else {
        echo "DB접속 실패";
    }
} else {
    echo "mysql접속 실패";
}

?>
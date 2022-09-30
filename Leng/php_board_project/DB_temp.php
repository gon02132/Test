<?php
@session_start();
if(isset($_GET['click_num'])) {
    $_SESSION['click_num'] = $_GET['click_num'];
}
echo $_SESSION['click_num'];
?>
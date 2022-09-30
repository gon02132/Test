<?php
@session_start();
//DB 정보 include
@include "databases_info.php";
//검색 기능 초기화 /현재 검색기록이있는지, 검색버튼element 갯수
$cyka=0;
$serch_str="";
$serch_option="title_serch";
$one_page_count=5;

//세션 생성,삭제 구간----------------------------------------------
if(!isset($_SESSION['page_swap'])){
    $_SESSION['page_swap']=0;
}
//이전에 검색기록기 있다면 가져다 쓰기
if(isset($_SESSION["serch_session"]) || isset($_SESSION["select_session"])){
    $serch_str = $_SESSION["serch_session"];
    $serch_option = $_SESSION["select_session"];
    $cyka =1;
}

//검색을 눌렀을시
if (isset($_GET['serch_str']) || isset($_GET['select_option'])){
    $_SESSION["serch_session"]=$_GET['serch_str'];
    $_SESSION["select_session"]=$_GET['select_option'];
    $serch_str = $_GET['serch_str'];
    $serch_option = $_GET['select_option'];
    $cyka =1;
    $_SESSION['page_swap'] = 0;
}

if(isset($_SESSION['id_session'])){
    echo $_SESSION['id_session']."<>login<>";
}
else{
    echo "2<>login<>";
}

//세션 끝----------------------------------------------------------
//페이지네이션을 위한 DB 검색
@$db_con = mysql_connect(HOST, USER, PASSWORD);
if ($db_con) {
    if (mysql_select_db(DB_NAME, $db_con)) {
        $serch_str = htmlspecialchars($serch_str);
        $serch_str = str_replace(" ", '&nbsp;', $serch_str);

        $query = "select * from " . TABLE_NAME;
        if ($serch_option == "title_serch") {
            $query = $query . " where title like '%" . $serch_str . "%'";
        } else if ($serch_option == "text_serch") {
            $query = $query . " where textarea like '%" . $serch_str . "%'";
        } else if ($serch_option == "person_serch") {
            $query = $query . " where user_name like '%" . $serch_str . "%'";
        } else if ($serch_option == "all_serch") {
            $query = $query . " where (title like '%" . $serch_str . "%' OR";
            $query = $query . " textarea like '%" . $serch_str . "%')";
        }
        $query = $query . " AND board_reid=0";

        if ($result = mysql_query($query)) {
            $_SESSION['all_table_counting'] = mysql_num_rows($result);
        }
    }
}

//페이지네이션일경우 DB탐색X
if(isset($_GET['page_num'])){
    //새로고침이나 글쓰기->목록 돌아올경우 세션호출 / 아닐경우 첫페이지로
    if($_GET['page_num']=="cyka"){

    }
    else {
        $_SESSION['page_swap'] = $_GET['page_num'];
    }
    echo $_SESSION['all_table_counting'] ."<>cnt<>".$one_page_count."<>cnt<>".$_SESSION['page_swap'];
}
else {
//임시
//db 접속
    @$db_con = mysql_connect(HOST, USER, PASSWORD);
    if ($db_con) {
        if (mysql_select_db(DB_NAME, $db_con)) {
            //글을 출력할건지 list를 출력할건지
            if (isset($_GET['temp'])) {
                //댓글을 출력할건지 글을 출력할건지
                if(isset($_GET['dat_check'])){
                    $query = "SELECT * from " . TABLE_NAME . " where  board_reid =" . $_GET['temp'] . " order by board_id DESC";
                }
                else {
                    $query = "SELECT * from " . TABLE_NAME . " where  board_id =" . $_GET['temp'];
                }
            } else if ($cyka == 1) { // 검색 이라면
                //특수문자(띄워쓰기 포함) 처리
                $serch_str = htmlspecialchars($serch_str);
                $serch_str = str_replace(" ", '&nbsp;', $serch_str);

                $query = "select * from " . TABLE_NAME;
                if ($serch_option == "title_serch") {
                    $query = $query . " where title like '%" . $serch_str . "%'";
                } else if ($serch_option == "text_serch") {
                    $query = $query . " where textarea like '%" . $serch_str . "%'";
                } else if ($serch_option == "person_serch") {
                    $query = $query . " where user_name like '%" . $serch_str . "%'";
                } else if ($serch_option == "all_serch") {
                    $query = $query . " where (title like '%" . $serch_str . "%' OR";
                    $query = $query . " textarea like '%" . $serch_str . "%')";
                }
                $query = $query . " AND board_reid=0";
            } else {
                $query = "SELECT * from " . TABLE_NAME . " where  board_reid = 0";
            }
            if (!isset($_GET['temp'])) {
                $query = $query . " order by date desc limit " . $_SESSION['page_swap'] * $one_page_count . "," . $one_page_count;
            }
            //echo "///////$query////////////";
            if ($result = mysql_query($query)) {
                //검색에 작성한것 유지
                echo $serch_str . "<>serch<>";
                echo $serch_option . "<>serch<>";
                echo $one_page_count."<>serch<>".$_SESSION['page_swap']."<>serch<>";
                //저장된 행의 갯수
                $rows = mysql_num_rows($result);
                echo $rows . "<>lens<>";

                //총 필드 갯수
                $fields = mysql_num_fields($result);
                //echo "////////$rows/////////";
                for ($i = 0; $i < $rows; $i++) {
                    $result_arr = mysql_fetch_row($result);
                    //글번호,제목,작성자,조회수,작성일 // ID
                    echo $result_arr[0] . "<>|<>" . $result_arr[2] . "<>|<>" . $result_arr[3] . "<>|<>" .
                        $result_arr[4] . "<>|<>" . $result_arr[5] . "<>|<>" . $result_arr[6];
                    if (isset($_GET['temp'])) {
                        //글 출력이 필요할경우 추가
                        echo "<>|<>" . $result_arr[7]."<>|<>".$result_arr[8];
                        //조회수 증가를위해 쿠키사용, 쿠키가 없을경우 조회수 카운팅 제한시간 1시간
                        //다른 아이디로 접속시 최초 1번 또한 카운팅 증가
                        if (!isset($_COOKIE[$_SESSION['id_session'] . $result_arr[0]])) {
                            //쿠키가 존재하지 않을경우
                            setcookie($_SESSION['id_session'] . $result_arr[0],
                                $_SESSION['id_session'] . $result_arr[0], time() + 3600);
                            $counting = $result_arr[4];
                            //echo "꾸끼없음";
                        } else {
                            //쿠키가이미 존재할경우
                            //echo "꾸끼있음";
                        }
                    }
                    //다음 글로 이동 명령어
                    echo "<>swp<>";
                }

            } else
                echo mysql_error($db_con);

            //조회수 증가 부분
            if (isset($counting) && (!isset($_GET['dat_check']))) {
                $counting = $counting + 1;
                $query = "UPDATE " . TABLE_NAME . " SET " . "hits=" . $counting . " where  board_id =" . $_GET['temp'];
                if ($result = mysql_query($query)) {
                }
            }
        } else {
            echo "DB접속 실패";
        }
        mysql_close($db_con);
    } else {
        echo "mysql접속 실패";
    }
}
?>
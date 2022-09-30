<?php
include_once 'model_folder/get_list.php';
class Model{
    private $list;
    public static $page_view_num = 5;         //한 페이지당 보여줄 게시글 수
    public static $pagenation_view_num = 5;   //한 페이지당 보여줄 페이지네이션 수
    private $db_con;                          //최초 1번 연결후 다른 클래스들이 공유하며 쓰는 db연결변수
    function __construct(){
//----------------------------------------DB접속 공통과정-----------------------------------------
        include_once "databases_info.php";
        @$this->db_con = new mysqli(HOST, USER, PASSWORD, DB_NAME);
        if(mysqli_connect_error()){
            echo mysqli_connect_errno().":".mysqli_connect_error();
        }
//-----------------------------------------초기세션관리------------------------------------------
        //페이지네이션 세션이 있는가?
        if(!isset($_SESSION['page_swap'])){
            $_SESSION['page_swap']=0;
        }
        //페이지네이션부분을 사용자가 클릭할경우
        if(isset($_GET['pagenaion_changed'])){
            $_SESSION['page_swap']=$_GET['pagenaion_changed'];
        }
//-----------------------------------------------------------------------------------------------
    }

    
    //테이블 리스트 가져오는 부분
    public function get_list(){
        //게시글 출력 부분
        $this->list = new write_list($this->db_con);

        //전체 게시글 탐색후 출력
        return $this->list->get_all_list();
    }

    //글의 총 갯수를 구해오는거
    public function get_all_num(){
        $this->list = new write_list($this->db_con);

        //총 게시글 갯수 출력
        return $this->list->get_all_nums();
    }

    public function close_DB(){
        mysqli_close($this->db_con);
    }

    public function get_db_con(){
        return $this->db_con;
    }
}
?>
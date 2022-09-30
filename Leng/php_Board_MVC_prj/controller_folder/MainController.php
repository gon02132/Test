<?php
@session_start();
include_once 'model_folder/Model.php';
include_once 'model_folder/id_check.php';

Class MainController{
    private $list;
    private $pagenation;
    private $print_pagenation;
    private $login_check;

    function __construct(){
        $this->list = new Model();
        //id의 관련 모든 함수가 들어가 있다. // 연결 상태를 가져와서 초기화한다
        $this->login_check = new id_check($this->list->get_db_con());
    }
    function start_fun(){
        //로그인,로그아웃을 판별한다
        $login_checked = $this->login_check->check_login();

        //model에서 게시글의 총 갯수를 가져온다
        $total_num = $this->list->get_all_num();

        include_once 'controller_folder/PagenationCon.php';
        //가져온 변수로 페이지네이션 객체를 만들고
        $this->pagenation = new PagenationCon($total_num);

        //view페이지네이션공간을 채운다
        $this->print_pagenation = $this->pagenation->get_pagenation();

        //model에서 DB자료를 가져오고
        $result = $this->list->get_list();

        //view에서 리스트화면을 가져온다
        include_once 'view_folder/view_main_page.php';
        //DB닫기
        $this->list->close_DB();
    }
}
?>
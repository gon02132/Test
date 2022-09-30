<?php
class write_list{
    private $result = array();      //DB에서 가져온 결과값이 들어갈 배열
    private $page_all_num;          //DB에서 글의 총 갯수를 구해온다
    private $db_con;
    function __construct($db_con_arg){

        //Model에서 DB연결 가져오기
        $this->db_con = $db_con_arg;

        //$arg_num = func_num_args(); // 인자 갯수
        //$arg_val = func_get_args(); //인자를 1차원 배열로 가져옴

    }
    
    //모든 리스트 출력
    function get_all_list(){
        //모든 리스트 출력

        $query = "SELECT * from " . TABLE_NAME . " where  board_reid = 0";
        //한페이지당 몇개를 보여줄것인지 추가 (주석처리시 전체 보여줌)
        $query = $query . " order by date desc limit " . $_SESSION['page_swap'] *
            Model::$page_view_num . "," . Model::$page_view_num;

        if ($result =  $this->db_con->query($query)) {
            //글 갯수 $rows
            $rows = mysqli_num_rows($result);
            for ($i = 0; $i < $rows; $i++) {
                $result_arr = mysqli_fetch_row($result);
                //select 한 결과값을 2차원 배열에 차곡차곡 넣는다
                $this->result[$i] = array();
                array_push($this->result[$i], $result_arr[0]);//게시글 번호
                array_push($this->result[$i], $result_arr[2]);//게시글 제목
                array_push($this->result[$i], $result_arr[3]);//작성자
                array_push($this->result[$i], $result_arr[4]);//조회수
                array_push($this->result[$i], $result_arr[5]);//수정일
            }
            return $this->result;
        }else
            echo mysqli_error($this->db_con);
    }

    function get_all_nums(){
        //모든 게시글 수 구하기
        $query = "SELECT * from " . TABLE_NAME . " where  board_reid = 0";
        if ($result =  $this->db_con->query($query)) {
            //글 총 갯수 반환
            $this->page_all_num = mysqli_num_rows($result);
        }else
            echo mysqli_error($this->db_con);

        return $this->page_all_num;

    }
}
?>
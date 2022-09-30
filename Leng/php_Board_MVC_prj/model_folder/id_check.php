<?php
class id_check{
    private $db_con;
    function __construct($db_con_arg){
        //연결상태 가져오기
        $this->db_con=$db_con_arg;
    }
    public function get_userName(){
        if(isset($_SESSION['user_id'])){
            //유저 정보를 확인한다.
            $query = "SELECT * from " . USERS_TABLE . " WHERE user_id='" . $_SESSION['user_id'] .
                "' AND user_password='" .  $_SESSION['user_pw'] . "'";
            if ($result =  $this->db_con->query($query)) {
                $result_arr = mysqli_fetch_array($result);
                return $result_arr[3];    // 유저 이름 반환
            }else{
                return mysqli_error($this->db_con);
            }
        }
    }
    public function check_login(){

        //로그인 버튼을 눌렀을시, 세션에 저장하고 유저닉네임을 반환하고 종료
        if (isset($_POST['user_id'])) {
            //ajax 반환이라 echo로 한다음 바로 종료를 하여야함(함수맨위에 선언)
            $_SESSION['user_id'] = $_POST['user_id'];
            $_SESSION['user_pw'] = $_POST['user_pw'];

            //닉네임 세션 생성
            $nickname =  $this->get_userName();
            $_SESSION['user_nickname'] = $nickname;

            echo $nickname;
            exit();
        }

        //로그아웃 버튼을 눌렀을 경우
        if(isset($_POST['logout_click'])){
            //ajax지만 POST로 받기위한것으로 echo 로 값을 전달해주지 않아도 됨
            //세션을 종료후 0반환
            unset($_SESSION['user_id']);
            unset($_SESSION['user_pw']);
            unset($_SESSION['user_nickname']);
            return 0;
        }
        //현재 로그인 중인가? 로그인중이면 유저닉네임 반환
        else if(isset($_SESSION['user_id'])){
            return $_SESSION['user_nickname'];
        }

    }
}
?>
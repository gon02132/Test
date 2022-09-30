<?php
class PagenationCon{
    private $page_all_num;
    private $print_pagenation = "";
    private $print_pagenation_arr = array();
    function __construct($total_num_arg){
        $this->page_all_num = $total_num_arg;
    }

    function get_pagenation(){
        //페이지네이션의 끝부분 초기화
        $page_tail_num = ceil((int)$this->page_all_num / (int)Model::$page_view_num);

        //페이지네이션 값을 사용자가 임의로 조정할경우 첫페이지로 귀환하는 예외처리
        if($_SESSION['page_swap']> $page_tail_num-1 || $_SESSION['page_swap']<0){
            $_SESSION['page_swap'] = 0;
        }

        //페이지네이션 시작부분 설정
        $pagenaion_start = $_SESSION['page_swap'];

        //페이지네이션의 끝부분(모든게시글수/게시글을 몇개씩 보여주나)
        //tail의 값이 계속바뀌는 변수 선언
        $page_tail_num_change = $page_tail_num;

        //MAX페이지를 넘어간다면 MAX이상 못가게 막기
        if((int)$page_tail_num_change>=(int)Model::$pagenation_view_num){
            $page_tail_num_change = (int)Model::$pagenation_view_num;
        }



        //번호 만드는 작업 --> 페이지출력수 만큼 반복
        //실제로 출력되는 숫자 변수 생성
        $print_count = (int)$pagenaion_start;

        //출력하고자하는 페이지 전체 갯수 - 페이지네이션 출력 갯수 즉 페이지네이션이 필요한 경우
        if((int)$page_tail_num-(int)Model::$pagenation_view_num >=0){
            //현재 페이지네이션이 페이지네이션 중간에 있는지 체크해주는 변수
            $middle_check = 0;
            //현재 페이지네이션이 1이 아니고 페이지네이션의 중간기준 왼쪽 부분의 경우
            if($pagenaion_start > 0 &&
                $pagenaion_start < $page_tail_num-(floor((int)Model::$pagenation_view_num/2))){

                //왼쪽부분 반복
                for($cnt=1; $cnt<floor((int)Model::$pagenation_view_num/2); $cnt++){
                    //페이지네이션 시작부분이 왼쪽부분에 있을경우
                    if((int)$pagenaion_start == $cnt){
                        $print_count -=  $cnt;
                        $middle_check = 1;
                    }
                }
                //중간 부분 일 경우
                if($middle_check != 1){
                    $print_count -= floor((int)Model::$pagenation_view_num/2);
                }
            }

            //현재 페이지네이션이 1이 아니고 오른쪽 부분에 왔을경우
            else if((int)$pagenaion_start !=0 &&
                (int)$pagenaion_start > (int)$page_tail_num-((floor(Model::$pagenation_view_num/2))+1)){
                //오른쪽 부분 반복
                for($cnt=floor((int)Model::$pagenation_view_num/2)+1;
                    $cnt<(int)Model::$pagenation_view_num; $cnt++){
                    //페이지네이션 끝부분 - 페이지네이션 시작부분 == 페이지네이션출력갯수-반복문
                    if((int)$page_tail_num-(int)$pagenaion_start == (int)Model::$pagenation_view_num-$cnt){
                        $print_count -= $cnt;
                    }
                }
            }
        }
        //페이지네이션이 필요없는 경우
        else{
            if($pagenaion_start > 0){
                for($cnt=1; $cnt<$page_tail_num; $cnt++){
                    if($pagenaion_start == $cnt){
                        $print_count -= $cnt;
                    }
                }
            }
        }

         array_push($this->print_pagenation_arr,$pagenaion_start);              //현재 페이지
         array_push($this->print_pagenation_arr,$print_count);                  //실제 출력되는 변수
         array_push($this->print_pagenation_arr,Model::$pagenation_view_num);   //반복 수
         array_push($this->print_pagenation_arr,$page_tail_num);                //마지막 페이지 위치

        return $this->print_pagenation_arr;
    }
}
?>
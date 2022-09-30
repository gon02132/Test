document.write("<script language=\"javascript\" src=\"login.js?var=2\"></script>");
//----------------------js 파일 import--------------------------------------------
PAGING_MAX = 5;

//list.html에서 onload시 호출하는 함수
function select_all(temp) {
    main_select(temp);
    login_check();
    pagenation("cyka");
}

//제목 클릭시
function hyper_click(hyper_num) {
    var xmlReqObj = new XMLHttpRequest();
    url = "DB_temp.php?click_num="+hyper_num;
    xmlReqObj.onreadystatechange = function () {
        if (xmlReqObj.readyState == 4 && xmlReqObj.status == 200) {
            location.href="see_text.html";
        }
    }
    xmlReqObj.open("GET", url, true);
    xmlReqObj.send();

}

//페이지네이션관련 함수
function pagenation(page_num) {
    var page_div = document.getElementById("pagenation_div");
    //div 비우기
    while(page_div.children.length>0){
        page_div.removeChild(page_div.lastChild);
    }
    page_div.innerHTML="";

    var xmlReqObj = new XMLHttpRequest();
    url = "DB_SELECT.php?page_num="+page_num;
    xmlReqObj.onreadystatechange = function () {
        if (xmlReqObj.readyState == 4 && xmlReqObj.status == 200) {
            var result = xmlReqObj.responseText.split("<>login<>");
            var cnt_arr = result[1].split("<>cnt<>");
            var t =0;
            var max_line= Math.ceil(Number(cnt_arr[0]) / Number(cnt_arr[1]));
            //0.모든게시글수 1.게시글을 몇개씩 보여주나 2. 현재 몇번째 페이지인가
            var page_line_view=max_line;
            //5페이지를 넘어가는가?
            if(page_line_view>=PAGING_MAX){
                page_line_view=PAGING_MAX;
            }
            //< 와 > 를 만드는 작업
            if(Number(cnt_arr[2])>0){
                var Create_button = document.createElement("text");
                Create_button.innerHTML = "<a style='color:#FF33CC;' href='javascript:;' " +
                    "onclick='pagenation(\" " + (0) + " \" )'>" + "\<\<" + "</a>";
                page_div.appendChild(Create_button);

            }
            else{ // 중앙정렬때문에 생기는 현상 제거
                var Create_button = document.createElement("text");
                Create_button.innerHTML = "&nbsp&nbsp";
                page_div.appendChild(Create_button);
            }

            if(Number(cnt_arr[2])>0){
                var Create_button = document.createElement("text");
                Create_button.innerHTML = "<a style='color:#FF33CC;' href='javascript:;' " +
                    "onclick='pagenation(\" " + (Number(cnt_arr[2]-1)) + " \" )'>" + "\<" + "</a>";
                page_div.appendChild(Create_button);

            }
            else{ // 중앙정렬때문에 생기는 현상 제거
                var Create_button = document.createElement("text");
                Create_button.innerHTML = "&nbsp&nbsp&nbsp&nbsp";
                page_div.appendChild(Create_button);
            }

            /*
             //------------------------임시
             var ttt = document.getElementById("ttqtt");
             ttt.innerHTML="0게시글수:"+cnt_arr[0]+"<br>";
             ttt.innerHTML+="1몇개씩?:"+cnt_arr[1]+"<br>";
             ttt.innerHTML+="2현재페이지(-1):"+cnt_arr[2]+"<br>";
             ttt.innerHTML+="max_line:"+max_line+"<br>";

             //----------------------임시
             */

            var now_page = Number(cnt_arr[2]);
            //페이지네이션 최대 PAGING_MAX번반복
            while(t<page_line_view) {
                var i = Number(cnt_arr[2]) + t;
                //출력하는 페이징수와 최대 페이징수보다 크거나 같다면
                if(max_line-PAGING_MAX>=0) {
                    if (now_page > 0 && now_page < max_line-(Math.floor(PAGING_MAX/2))) {
                        //페이지네이션클릭이 중간기준 왼쪽 혹은 중앙에 욌을시
                        var temp_cnt = 0;
                        for(var n=1; n<Math.floor(PAGING_MAX/2); n++){
                            if (now_page == n) {
                                i = i - n;
                                temp_cnt=1;
                            }
                        }
                        //중간에 왔을시
                        if(temp_cnt!=1) {
                            i = i - Math.floor(PAGING_MAX/2);
                        }
                    }
                    //페이지네이션 중간기준 오른쪽
                    else if (now_page != 0 && now_page > max_line-((Math.floor(PAGING_MAX/2))+1)){
                        for(var n=Math.floor(PAGING_MAX/2)+1; n<PAGING_MAX; n++){
                            if(max_line-now_page==PAGING_MAX-n){
                                i=i-n;
                            }
                        }
                    }
                }
                //MAX로 요구하는페이지보다 작을페이지일경우
                else{
                    if (now_page > 0){
                        for(var q=1; q<max_line; q++){
                            if(now_page == q){
                                i = i - q;
                            }
                        }
                    }
                }
                var Create_button = document.createElement("text");
                if (now_page == i) {
                    Create_button.innerHTML = "<a style='color:red;' href='javascript:;' " +
                        "onclick='pagenation(\" " + i + " \" )'>" + (i + 1) + "</a>";
                    page_div.appendChild(Create_button);
                }
                else {
                    Create_button.innerHTML = "<a href='javascript:;' " +
                        "onclick='pagenation(\" " + i + " \" )'>" + (i + 1) + "</a>";
                    page_div.appendChild(Create_button);
                }

                t++;
            }

            if(now_page<max_line-1){
                var Create_button = document.createElement("text");
                Create_button.innerHTML = "<a style='color:#FF33CC;' href='javascript:;' " +
                    "onclick='pagenation(\" " + (now_page+1) + " \" )'>" + "\>" + "</a>";
                page_div.appendChild(Create_button);

            }
            else{ // 중앙정렬때문에 생기는 현상 제거
                var Create_button = document.createElement("text");
                Create_button.innerHTML = "&nbsp&nbsp";
                page_div.appendChild(Create_button);
            }

            if(now_page<max_line-1){
                var Create_button = document.createElement("text");
                Create_button.innerHTML = "<a style='color:#FF33CC;' href='javascript:;' " +
                    "onclick='pagenation(\" " + (max_line-1) + " \" )'>" + "\>\>" + "</a>";
                page_div.appendChild(Create_button);

            }
            else{ // 중앙정렬때문에 생기는 현상 제거
                var Create_button = document.createElement("text");
                Create_button.innerHTML = "&nbsp&nbsp&nbsp&nbsp";
                page_div.appendChild(Create_button);
            }
            main_select();
        }
    }
    xmlReqObj.open("GET", url, true);
    xmlReqObj.send();

}

function main_select(temp) {
    var xmlReqObj = new XMLHttpRequest();
    var list_id = document.getElementById("tbody_cyka");
    var count = 1;
    var write_div = document.getElementById("write_div");

    //php 받아온 데이터를 나눠서 테이블에 넣기
    xmlReqObj.onreadystatechange = function () {
        if (xmlReqObj.readyState == 4 && xmlReqObj.status == 200) {
            //글쓰기 버튼 삭제
            while (write_div.children.length > 0) {
                write_div.removeChild(write_div.lastChild);
            }
            //테이블 초기화
            while (list_id.children.length > 0) {
                list_id.removeChild(list_id.lastChild);
            }
            var result = xmlReqObj.responseText;
            //alert(result);
            var login_check = result.split("<>login<>");
            var serch_text = login_check[1].split("<>serch<>");

            //로그인이 되어있는 상태라면 글쓰기 버튼 생성
            if (login_check[0] != 2) {
                var create_write_button = document.createElement("input");
                create_write_button.setAttribute("type", "button");
                create_write_button.setAttribute("onclick", "location.href=\"write.html\"");
                create_write_button.setAttribute("class", "btn btn-sm btn-info");
                create_write_button.setAttribute("value", "글쓰기");
                write_div.appendChild(create_write_button);
            }

            document.getElementById("serch_str").value = serch_text[0].replace(/&nbsp;/g, " ");
            ;
            document.getElementById(serch_text[1]).selected = "selected";
            count = Number(serch_text[2])*((Number(serch_text[3])));
            count++;
            var result_len = serch_text[4].split("<>lens<>");
            for (var i = 0; i < result_len[0]; i++) {
                var result_rows = result_len[1].split("<>swp<>");
                var result_filed = result_rows[i].split("<>|<>");

                var Create_TR = document.createElement("TR");
                //마지막 id를 제외한 길이를 테이블에 붙여넣기
                for (var j = 0; j < result_filed.length - 1; j++) {
                    var Create_TD = document.createElement("TD");
                    //제목부분 하이퍼링크 달기
                    if (j == 0) {
                        Create_TD.innerHTML = count++;
                    }
                    else if (j == 1) {
                        if (login_check[0] == 2) {
                            Create_TD.innerHTML = result_filed[j];
                        }
                        else {
                            Create_TD.innerHTML = result_filed[j];
                            Create_TD.innerHTML = "<a href='javascript:;' " +
                                "onclick='hyper_click(\" " + result_filed[0] + " \" )'>" + result_filed[j] + "</a>";
                        }
                    }
                    else {
                        Create_TD.innerHTML = result_filed[j];
                    }
                    Create_TR.appendChild(Create_TD);
                }
                list_id.appendChild(Create_TR);
            }
        }
    }
    //인자를 아무거도 안받았을시 모두 표시
    if (temp == undefined) {
        url = "DB_SELECT.php";
        xmlReqObj.open("GET", url, true);
    }
    //검색버튼을 눌렀을 시
    if (temp == "1") {
        url = "DB_SELECT.php?serch_str=" + document.getElementById("serch_str").value +
            "&select_option=" + document.getElementById("select_option").value;
        xmlReqObj.open("GET", url, true);
    }
    else {
    }
    xmlReqObj.send();
}
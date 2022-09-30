function main_see() {
    var xmlReqObj = new XMLHttpRequest();
    url = "DB_temp.php";
    xmlReqObj.onreadystatechange = function () {
        if (xmlReqObj.readyState == 4 && xmlReqObj.status == 200) {
            select_see(xmlReqObj.responseText);
        }
    }
    xmlReqObj.open("GET", url, true);
    xmlReqObj.send();
}
function alter_table(line_num, temp) {
    var xmlReqObj = new XMLHttpRequest();
    url = "DB_ALTER.php";
    xmlReqObj.onreadystatechange = function () {
        if (xmlReqObj.readyState == 4 && xmlReqObj.status == 200) {
            if(temp!=undefined){
                select_see(temp);
            }
            else {
                select_see(line_num);
            }
        }
    }
    if(temp == undefined) {
        if (document.getElementsByName("title_area_name")[0].value == "") {
            document.getElementsByName("title_area_name")[0].value = "Blank";
        }
    }
    xmlReqObj.open("POST", url, true);
    xmlReqObj.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
    //글 수정시
    if(temp == undefined) {
        xmlReqObj.send("text_area_name=" + document.getElementsByName("text_area_name")[0].value +
            "&see_cyka=" + line_num +
            "&title_area_name=" + document.getElementsByName("title_area_name")[0].value);
    }
    //댓글 수정시
    else{
        if(document.getElementsByName("text_area_name")[0].value==""){

        }
        else {
            xmlReqObj.send("text_area_name=" + document.getElementsByName("text_area_name")[0].value +
                "&see_cyka=" + line_num);
        }
    }
}

function delete_table(line_num, temp) {
    var xmlReqObj = new XMLHttpRequest();
    url = "DB_DELETE.php?temp="+line_num;
    xmlReqObj.onreadystatechange = function () {
        if (xmlReqObj.readyState == 4 && xmlReqObj.status == 200) {
            if(temp != undefined){
                select_see(temp);
            }
            else {
                location.href = "list.html";
            }
        }
    }
    xmlReqObj.open("GET", url, true);
    xmlReqObj.send();
}

function dat_write(text_num) {
    var xmlReqObj = new XMLHttpRequest();
    url = "DB_dat_INSERT.php";
    xmlReqObj.onreadystatechange = function () {
        var result = xmlReqObj.responseText;
        //alert(result);
        select_see(text_num);

    }
    //alert(text_num);
    xmlReqObj.open("POST", url, true);
    if(document.getElementsByName("dat_area")[0].value == ""){
    }
    else {
        xmlReqObj.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
        xmlReqObj.send("text_num=" + text_num + "&text_area=" + document.getElementsByName("dat_area")[0].value);
        document.getElementsByName("dat_area")[0].value = "";
    }

}

function repair_text_f(line_num){
    var xmlReqObj = new XMLHttpRequest();
    url = "DB_SELECT.php?temp="+line_num;
    xmlReqObj.onreadystatechange = function () {
        if (xmlReqObj.readyState == 4 && xmlReqObj.status == 200) {
            var result = xmlReqObj.responseText;
            //alert(result);
            var login_check = result.split("<>login<>");
            var serch_text = login_check[1].split("<>serch<>");
            var result_len = serch_text[4].split("<>lens<>");
            for (var i = 0; i < result_len[0]; i++) {
                var result_rows = result_len[1].split("<>swp<>");
                var result_filed = result_rows[i].split("<>|<>");
                document.getElementById("text_area").innerHTML=result_filed[6];
            }
            var td_area = document.getElementById("title");
            var tx_ar=document.createElement("textarea");
            tx_ar.setAttribute("name","title_area_name");
            tx_ar.setAttribute("cols","20");
            tx_ar.setAttribute("rows","2");
            //정규 표현식을 이용한 개행문자 제거
            tx_ar.innerHTML=td_area.innerHTML.replace("제목:","");
            td_area.innerHTML="";
            td_area.appendChild(tx_ar);

            td_area = document.getElementById("text_area");
            td_area.innerHTML="";
            var tx_ar=document.createElement("textarea");
            tx_ar.setAttribute("name","text_area_name");
            tx_ar.setAttribute("cols","98");
            tx_ar.setAttribute("rows","30");
            //정규 표현식을 이용한 개행문자 제거
            tx_ar.innerHTML=result_filed[6].replace(/<br \/>/gi,"");
            td_area.appendChild(tx_ar);

            //옵션바 바꾸기
            document.getElementById("back_list").innerHTML = "";
            document.getElementById("repair").innerHTML = "";
            document.getElementById("delete").innerHTML = "";

            var a_temp = document.createElement("a");
            a_temp.setAttribute("id","submit");
            a_temp.setAttribute("href","javascript:;");
            a_temp.setAttribute("onclick","alter_table("+line_num+")");
            a_temp.innerHTML="확인";
            document.getElementById("back_list").appendChild(a_temp);

            a_temp = document.createElement("a");
            a_temp.setAttribute("id","cancel");
            a_temp.setAttribute("href","javascript:;");
            a_temp.setAttribute("onclick","select_see("+line_num+")");
            a_temp.innerHTML="취소";
            document.getElementById("repair").appendChild(a_temp);

        }
    }
    xmlReqObj.open("GET", url, true);
    xmlReqObj.send();

}

function dat_repair(dat_num, original){
    var xmlReqObj = new XMLHttpRequest();
    url = "DB_SELECT.php?temp="+dat_num;
    xmlReqObj.onreadystatechange = function () {
        if (xmlReqObj.readyState == 4 && xmlReqObj.status == 200) {
            var result = xmlReqObj.responseText;
            //alert(result);
            var login_check = result.split("<>login<>");
            var serch_text = login_check[1].split("<>serch<>");
            var result_len = serch_text[4].split("<>lens<>");
            for (var i = 0; i < result_len[0]; i++) {
                var result_rows = result_len[1].split("<>swp<>");
                var result_filed = result_rows[i].split("<>|<>");
                document.getElementById("dat_text"+dat_num).innerHTML=result_filed[6];
            }

            var td_area = document.getElementById("dat_text"+dat_num);
            td_area.innerHTML="";
            var tx_ar=document.createElement("textarea");
            tx_ar.setAttribute("name","text_area_name");
            tx_ar.setAttribute("cols","40");
            tx_ar.setAttribute("rows","10");
            //정규 표현식을 이용한 개행문자 제거
            tx_ar.innerHTML=result_filed[6].replace(/<br \/>/gi,"");
            td_area.appendChild(tx_ar);

            //옵션바 바꾸기
            document.getElementById(dat_num+"dat_alter").innerHTML = "";
            document.getElementById(dat_num+"dat_delete").innerHTML = "";

            var a_temp = document.getElementById(dat_num+"dat_alter");
            a_temp.setAttribute("id","submit");
            a_temp.setAttribute("href","javascript:;");
            a_temp.setAttribute("onclick","alter_table("+dat_num+","+ original +")");
            a_temp.innerHTML="확인";

            a_temp = document.getElementById(dat_num+"dat_delete");
            a_temp.setAttribute("id","cancel");
            a_temp.setAttribute("href","javascript:;");
            a_temp.setAttribute("onclick","select_see("+original+")");
            a_temp.innerHTML="취소";

        }
    }
    xmlReqObj.open("GET", url, true);
    xmlReqObj.send();
}
//댓글의 댓글 DB에 넣기
function dat_dat_insert(dat_num, original) {
    var xmlReqObj = new XMLHttpRequest();
    url = "DB_dat_INSERT.php";
    xmlReqObj.onreadystatechange = function () {
        if (xmlReqObj.readyState == 4 && xmlReqObj.status == 200) {
            var result = xmlReqObj.responseText;
            dat_see(original);
        }
        else{
            //alert(xmlReqObj.responseText);
        }
    }
    //alert(text_num);
    xmlReqObj.open("POST", url, true);
    xmlReqObj.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
    xmlReqObj.send("text_num="+original+"&dat_num="+dat_num+"&text_area="+document.getElementsByName("dat_dat_area")[0].value);
}

//인자: 댓글num,현재있는페이지의num
function dat_dat(dat_num, original) {
//<textarea cols="90" name="dat_area"></textarea>
    //댓글에 댓글의 textarea 부분
    var Dat_TR = document.getElementById(dat_num);
    while(Dat_TR.children.length>5){
        Dat_TR.removeChild(Dat_TR.lastChild);
    }

    var Create_TD = document.createElement("TD");
    var Create_TextArea=document.createElement("textarea");

    Create_TD.setAttribute("colspan","3");
    Create_TextArea.setAttribute("cols","80");
    Create_TextArea.setAttribute("name","dat_dat_area");
    Create_TD.appendChild(Create_TextArea);
    Dat_TR.appendChild(Create_TD);

    //댓글의 댓글 전송
    Create_TD = document.createElement("TD");
    Create_TD.innerHTML = "<a href='javascript:;' onclick='dat_dat_insert(" + dat_num +"," + original + ")'> &nbsp&nbsp쓰기 </a>"+"&nbsp|";
    Dat_TR.appendChild(Create_TD);
    Create_TD = document.createElement("TD");
    Create_TD.innerHTML = "<a href='javascript:;' onclick='dat_see(" + original + ")'> &nbsp&nbsp취소</a>";
    Dat_TR.appendChild(Create_TD);
}

function dat_see(temp){
    var dat_arr_push=[];
    var xmlReqObj = new XMLHttpRequest();
    url = "DB_SELECT.php?temp="+temp+"&dat_check=1";
    xmlReqObj.onreadystatechange = function () {
        if (xmlReqObj.readyState == 4 && xmlReqObj.status == 200) {
            var result = xmlReqObj.responseText;
            //alert(result);

            var login_check = result.split("<>login<>");
            var serch_text = login_check[1].split("<>serch<>");
            var result_len = serch_text[4].split("<>lens<>");

            var dat_space = document.getElementById("dat_space");
            while(dat_space.children.length>0){
                dat_space.removeChild(dat_space.lastChild);
            }
            //작성자,날짜,내용 순
            for (var i = 0; i < result_len[0]; i++) {
                var login_check = result.split("<>login<>");
                var serch_text = login_check[1].split("<>serch<>");
                var result_len = serch_text[4].split("<>lens<>");
                var result_rows = result_len[1].split("<>swp<>");
                var result_filed = result_rows[i].split("<>|<>");

                if (result_filed[7] !=0) {
                    dat_arr_push.unshift(result_filed);
                    //unshift는 첫자리를 넣는거기때문에 [0]으로,push로 각배열의 마지막자리에 logincheck삽입
                    dat_arr_push[0].push(login_check[0]);
                }
                else {
                    var Create_DIV = document.createElement("DIV");
                    Create_DIV.setAttribute("id", result_filed[0]);
                    //작성자
                    Create_DIV.innerHTML = "작성자:" + result_filed[2] + " | ";
                    //날짜
                    Create_DIV.innerHTML += result_filed[4] + " | ";
                    Create_DIV.innerHTML += "<a href='javascript:;' onclick='dat_dat(" + result_filed[0] + "," + temp + ")'> 댓글달기 </a> | ";

                    if (login_check[0] == result_filed[5]) {
                        Create_DIV.innerHTML += "<a id="+result_filed[0]+"dat_alter href='javascript:;' onclick='dat_repair(" + result_filed[0] + "," + temp + ")'> 수정 </a> | ";

                        Create_DIV.innerHTML += "<a id="+result_filed[0]+"dat_delete href='javascript:;' onclick='delete_table(" + result_filed[0] + "," + temp + ")'> 삭제 </a>  ";
                    }
                    //내용
                    Create_DIV.innerHTML += "<br>";
                    dat_space.appendChild(Create_DIV);

                    Create_DIV = document.createElement("DIV");
                    Create_DIV.setAttribute("id", "dat_text" + result_filed[0]);
                    Create_DIV.innerHTML += result_filed[6];
                    document.getElementById(result_filed[0]).appendChild(Create_DIV);
                }
            }


            for(var i=0; i<dat_arr_push.length; i++) {
                var original_dat = document.getElementById(dat_arr_push[i][7]);
                if (original_dat != undefined) {
                    var Create_DIV = document.createElement("DIV");
                    Create_DIV.setAttribute("id", dat_arr_push[i][0]);
                    // var temp = document.getElementById("style");

                    Create_DIV.setAttribute("style", "padding-left:20px");
                    //작성자
                    Create_DIV.innerHTML = "ㄴ작성자:" + dat_arr_push[i][2] + " | ";
                    //날짜
                    Create_DIV.innerHTML += dat_arr_push[i][4] + " | ";
                    Create_DIV.innerHTML += "<a href='javascript:;' onclick='dat_dat(" + dat_arr_push[i][0] + "," + temp + ")'> 댓글달기 </a> | ";
                    if (dat_arr_push[i][8] == dat_arr_push[i][5]) {
                        Create_DIV.innerHTML += "<a id="+dat_arr_push[i][0]+"dat_alter href='javascript:;' onclick='dat_repair(" + dat_arr_push[i][0] + "," + temp + ")'> 수정 </a> | ";

                        Create_DIV.innerHTML += "<a id="+dat_arr_push[i][0]+"dat_delete href='javascript:;' onclick='delete_table(" + dat_arr_push[i][0] + "," + temp + ")'> 삭제 </a>  ";
                    }
                    //내용
                    Create_DIV.innerHTML += "<br>";
                    original_dat.appendChild(Create_DIV);

                    Create_DIV = document.createElement("DIV");
                    Create_DIV.setAttribute("style", "padding-left:15px");
                    Create_DIV.setAttribute("id", "dat_text" + dat_arr_push[i][0]);
                    Create_DIV.innerHTML += dat_arr_push[i][6];
                    document.getElementById(dat_arr_push[i][0]).appendChild(Create_DIV);
                }
            }
        }
    }
    xmlReqObj.open("GET", url, true);
    xmlReqObj.send();
}
function select_see(temp) {
    var xmlReqObj = new XMLHttpRequest();
    url = "DB_SELECT.php?temp="+temp;
    xmlReqObj.onreadystatechange = function () {
        if (xmlReqObj.readyState == 4 && xmlReqObj.status == 200) {
            var result = xmlReqObj.responseText;
            var login_check = result.split("<>login<>");
            var serch_text = login_check[1].split("<>serch<>");
            var result_len = serch_text[4].split("<>lens<>");
            var result_rows = result_len[1].split("<>swp<>");
            var result_filed = result_rows[0].split("<>|<>");

            //제목,작성자,조회수,날짜,내용
            document.getElementById("title").innerHTML = "제목:" + result_filed[1];
            document.getElementById("user_name").innerHTML = "작성자:" + result_filed[2];
            document.getElementById("his").innerHTML = "조회수:" + result_filed[3];
            document.getElementById("date").innerHTML = "작성/수정 날짜:" + result_filed[4];
            document.getElementById("text_area").innerHTML = result_filed[6];
            document.getElementsByName("see_cyka")[0].value = result_filed[0];
            var back_list_text = document.getElementById("back_list");
            var repair_text = document.getElementById("repair");
            var delete_text = document.getElementById("delete");
            back_list_text.innerHTML = "<a href='javascript:;' onclick='location.href= \"list.html\" '> 목록 </a>";
            //현재 유저id와 글쓴이의id를 비교해서 맞으면 수정과 삭제가 가능하도록
            if (login_check[0] == result_filed[5]) {
                repair_text.innerHTML = "<a href='javascript:;' onclick='repair_text_f(" + temp + ")'> 수정 </a>";
                delete_text.innerHTML = "<a href='javascript:;' onclick='delete_table(" + temp + ")'> 삭제 </a>";
            }
            else{
                document.getElementById("repair").innerHTML="";
                document.getElementById("delete").innerHTML="";

            }
        }
    }
    xmlReqObj.open("GET", url, true);
    xmlReqObj.send();
    //댓글리스트보기
    dat_see(temp);
}
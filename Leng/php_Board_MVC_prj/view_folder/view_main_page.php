<!doctype html>
<html lang="ko">
<head>
    <meta charset="utf-8">
    <?php include "style_all.html"; ?>
    <script src="https://code.jquery.com/jquery-3.2.1.js"></script>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.2/css/bootstrap.min.css">

    <!-- 부가적인 테마 -->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.2/css/bootstrap-theme.min.css">

    <!-- 합쳐지고 최소화된 최신 자바스크립트 -->
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.2/js/bootstrap.min.js"></script>
</head>
<body>
<div id="main_div">
    <div id="title_div">
        <button type="button" class="btn btn-lg btn-default">!!!!!!!!!dog see pan!!!!!!!!!</button>
    </div>
    <div id="left_div">
        <div id="login_div">
            <?php include_once "view_login.php"; ?>
        </div>
    </div>
    <div id="list_div">
        <div id="serch_div">
            <select id="select_option" class="form-control">
                <option id="title_serch" value="title_serch">제목만</option>
                <option id="text_serch" value="text_serch">내용만</option>
                <option id="person_serch" value="person_serch">작성자</option>
                <option id="all_serch" value="all_serch">전부</option>
            </select>
            <input type='text' id = "serch_str" class="form-control" placeholder="검색 키워드">
            <input type='button' value="검색" onclick="select_all(1)" class="btn btn-sm btn-danger">
            <div id="write_div">
                <?php include_once "view_write.php"?>
            </div>
        </div>
        <div id="list_main">
            <table border="1px" style="border-collapse: collapse" id="table_list" align="center" class="table table-striped">
                <thead>
                <tr align="center" bgcolor="#04B45F">
                    <td width="50px">번호</td>
                    <td width="250px"> 제목</td>
                    <td width="150px">작성자</td>
                    <td width="62px">조회수</td>
                    <td width="150px">작성일/최근 수정일</td>
                </tr>
                </thead>
                <tbody>
                <?php
                $count = 1;
                foreach($result as $lists){
                    echo "<tr><td>$lists[0]</td><td id='list_{$count}'>$lists[1]</td><td>$lists[2]</td>
                              <td>$lists[3]</td><td>$lists[4]</td></tr>";
                    $count++;
                }
                ?>
                </tbody>
            </table>
        </div>
        <div id="pagenation_div">
            <?php include_once 'view_pagenation.php' ?>
        </div>
    </div>
</div>
</body>
</html>
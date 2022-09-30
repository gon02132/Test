<?php
//<<--------------------------------------------------------------------------------------------------------
//<< 부분
// 배열 : 0현재 페이지, 1실제 출력되는 변수, 2반복 수 , 3마지막 페이지 위치
if($this->print_pagenation[0]>0){
    echo "<a style='color:#FF33CC;' href='index.php?pagenaion_changed=0'><<<a> ";
    echo "&nbsp";
}else{
    echo "&nbsp&nbsp";
}

//< 부분
if($this->print_pagenation[0]>0){
    echo "<a style='color:#FF33CC;' href='index.php?pagenaion_changed=".($this->print_pagenation[0] - 1)."'><<a>";
    echo "&nbsp&nbsp";
}else{
    echo "&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp";
}
//<<--------------------------------------------------------------------------------------------------------

//숫자부분
for($cnt = 0; $cnt<$this->print_pagenation[2]; $cnt++){
    if($this->print_pagenation[0] == $this->print_pagenation[1]+$cnt){
        echo "<a style='color:red;' href='index.php?pagenaion_changed=" . ($this->print_pagenation[1] + $cnt) . "'>" . ($this->print_pagenation[1] + $cnt + 1) . "<a> ";
    }else {
        echo "<a href='index.php?pagenaion_changed=" . ($this->print_pagenation[1] + $cnt) . "'>" . ($this->print_pagenation[1] + $cnt + 1) . "<a> ";
    }
}

//>>--------------------------------------------------------------------------------------------------------
//> 부분
if($this->print_pagenation[0]<$this->print_pagenation[3]-1) {
    echo "&nbsp&nbsp";
    echo "<a style='color:#FF33CC;' href='index.php?pagenaion_changed=" . ($this->print_pagenation[0] + 1) . "'>><a> ";
    echo "&nbsp";
}else {
    echo "&nbsp&nbsp";
}

//>> 부분
if($this->print_pagenation[0]<$this->print_pagenation[3]-1) {
    echo "<a style='color:#FF33CC;' href='index.php?pagenaion_changed=".($this->print_pagenation[3]-1)."'>>><a>";
}else{
    echo "&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp";
}
//>>--------------------------------------------------------------------------------------------------------
?>
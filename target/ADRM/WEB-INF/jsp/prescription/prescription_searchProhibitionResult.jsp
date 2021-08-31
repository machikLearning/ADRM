<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>

<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="UTF-8">
	<meta http-equiv="Content-Script-Type" content="text/javascript">
	<meta http-equiv="Content-Style-Type" content="text/css">
	<link rel="shortcut icon" href="<c:url value="/img/logo.png"/>">
	<title>ADRM</title>
	<link rel="stylesheet" type="text/css" href="<c:url value="/css/normalize.css"/>">
	<link rel="stylesheet" type="text/css" href="<c:url value="/css/layout.css"/>">
	<link rel="stylesheet" type="text/css" href="<c:url value="/css/font.css"/>">
	<script src="//ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>
	<script type="text/javascript">
		$(document).ready(function(){
			$("#searchButton").click(function(){
				if($("#searchValue").val() == ""){
					alert("검색어를 바르게 입력하세요(공백X).");
					return false;
				}
				$("#searchForm").submit();
			});
			$(window).resize(function(){
				popResizer();
			});
		});
		function popResizer(){
		    window.resizeTo(1024,800);
		};
		popResizer();
		
		function checkStatus(prohibitionCode){
			var status = 0;
			$("td[name=registrationProhibitionCode]",opener.document).each(function(i,e){
				if($(this).text() == prohibitionCode){
					status = 1;	
				}
			});
			$("td[name=prohibitionCode]",opener.document).each(function(i,e){
				if($(this).text() == prohibitionCode){
					status = 2;
				}
			});
			$("td[name=registrationUpperCode]",opener.document).each(function(i,e){
				if($(this).text() == prohibitionCode){
					status = 3;
				}
			});
			$("td[name=upperCode]",opener.document).each(function(i,e){
				if($(this).text() == prohibitionCode){
					status = 4;
				}
			});
			$("td[name=registrationTolerableCode]",opener.document).each(function(i,e){
				if($(this).text() == prohibitionCode){
					status = 5;	
				}
			});
			$("td[name=tolerableCode]",opener.document).each(function(i,e){
				if($(this).text() == prohibitionCode){
					status = 6;	
				}
			});
			return status;
		}
		
		function appendParent(prohibitionCode, prohibitionName){
			var html = "<tr>";
			html += "<td name='prohibitionCode'>" + prohibitionCode + "</td>";
			html += "<td name = 'prohibitionName'>" + prohibitionName + "</td>";
			html += "<td><input type=button value='삭제' class='btn_save' onClick='removeRow(this)'</td>";
			html += "</tr>";
			$(opener.document).find("#prohibitionTableBody").append(html);
			close();
		
		}
		
		function selectProhibition(prohibitionCode, prohibitionName) {
			var status = checkStatus(prohibitionCode);
			switch(status){
				case 1:
					alert("이미 금지목록에 추가되어 있습니다");
					break;
				case 2:
					alert("이미 금지목록에 추가되어 있습니다");
					break;
				case 3:
					alert("주의목록에 추가되어 있는 항목입니다 금지약물에 추가하시겠습니까?");
					break;
				case 4:
					alert("주의목록에 추가되어 있는 항목입니다 금지약물에 추가하시겠습니까?");
					break;
				case 5:
					alert("복용목록에 추가되어 있던 항목입니다");
					break;
				case 6:
					alert("복용목록에 추가되어 있던 항목입니다");
					break;
				default:
					appendParent(prohibitionCode, prohibitionName);
			}
		}
	</script>
</head>
<body style=" min-width: 1024px;">
	<div id="searchtab">
	<div class="searchtab_ti">금지 약제 검색</div>
		<form id="searchProhibition" name="searchProhibition" class="search_info"  style="margin:0 auto;"  action="<c:url value="/prescription/searchProhibitionResult"/>" method="POST">
			<li>
				<select name="searchOption" class="searchOption">
					<option value="medicineFullName"<c:if test="${searchpaging.searchOption == 'medicineFullName'}">selected</c:if>>약제 이름</option>
					<option value="medicineCode"<c:if test="${searchpaging.searchOption == 'medicineCode'}">selected</c:if>>약제 코드</option>
					<option value="ATCname"<c:if test="${searchpaging.searchOption == 'ATCname'}">selected</c:if>>ATC성분</option>
					<option value="ATCcode"<c:if test="${searchpaging.searchOption == 'ATCcode'}">selected</c:if>>ATC코드</option>
				</select>
				<input type="text" id="searchValue" name="searchValue" class="inputText" placeholder="조건 입력" value="${searchpaging.searchValue}"/>
				<input type="hidden" id="date" name="date" value="${date}"/>
				<input type="submit" id="searchButton" name="searchButton" value="검색" class="submitbtn"/>
			</li>
		</form>
		
		<!-- table :s -->
		<table class="tbl_basic center" id="medicineTable" style="table-layout:fixed;">
	          <caption>
	              <strong>금기 약제 검색 결과</strong>
	              <details>
	                  <summary>금기 약제 검색 결과</summary>
	              </details>
	          </caption>
	             <thead>
	                 <tr>
						<th class="trw">ATC코드</th>
						<th class="trw">ATC성분</th>
						<th class="trw">선택</th>
	                 </tr>
	             </thead>
	             <tbody>
	             	<c:choose>
	             		<c:when test="${fn:length(atcList) > 0 }">
	             			<c:set var="cnt" value="${searchpaging.startRow }"/>
			             	<c:forEach var="atc" items="${atcList}">
							<c:set var="cnt" value="${cnt+1 }"/>
							<tr>
								<td><c:out value="${atc.code }"/></td>
								<td><c:out value="${atc.levelName }"/></td>
								<td><input type="button" value="선택" class="btn_save" onclick="selectProhibition('<c:out value="${atc.code }"/>','<c:out value="${atc.levelName }"/>')"/></td>
							</tr>
							</c:forEach>
	             		</c:when>
	             		<c:otherwise>
	             		<tr>
	             			<td colspan="3">해당 조건의 약품이 검색되지 않습니다.</td>
	             		</tr>
	             		</c:otherwise>
	             	</c:choose>
			</tbody>
		</table>
		<!-- table :e -->           
		<!-- searchpaging & search :s -->
		<ul class="paginate">
			<c:if test="${searchpaging.nowPageGroup > 1 }">
				<li class="dir prev">
					<a href="<c:url value="/prescription/searchProhibitionResult" >
					<c:param name="page" value="${(searchpaging.nowPageGroup-2)*searchpaging.pageGroupSize+1 }"/>
					<c:param name="searchOption" value="${searchpaging.searchOption}" />
					<c:param name="searchValue" value="${searchpaging.searchValue}" />
					<c:param name="date" value="${date}" />
					</c:url>" onclick="" title="이전페이지로 이동">«</a>
				</li>
			</c:if>
			<c:forEach var="i" begin="${searchpaging.startPage }" end="${searchpaging.endPage }">
				<c:choose>
					<c:when test="${param.page == i }">
						<li class="active">
					</c:when>
					<c:otherwise>
						<li>
					</c:otherwise>
				</c:choose>
				<a href="<c:url value="/prescription/searchProhibitionResult" >
				<c:param name="page" value="${i }"/>
				<c:param name="searchOption" value="${searchpaging.searchOption}" />
				<c:param name="searchValue" value="${searchpaging.searchValue}" />
				<c:param name="date" value="${date}" />
				</c:url>" title="<c:out value="${i }"/>페이지" onclick=" ">
				<c:out value="${i }"/>
				</a>
				</li>
			</c:forEach>
			<c:if test="${searchpaging.nowPageGroup < searchpaging.pageGroupCount }" >
     			<li class="dir next"><a href="<c:url value="/prescription/searchProhibitionResult">
      			<c:param name="page" value="${searchpaging.nowPageGroup*searchpaging.pageGroupSize+1 }"/>
      			<c:param name="searchOption" value="${searchpaging.searchOption}" />
      			<c:param name="searchValue" value="${searchpaging.searchValue}" />
      			<c:param name="date" value="${date}" />
      			</c:url>" onclick=" " title="다음페이지로 이동">»</a></li>
       		</c:if>
   		</ul>
	</div>
</body>
</html>

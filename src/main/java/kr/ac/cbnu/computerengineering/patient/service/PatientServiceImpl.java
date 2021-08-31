package kr.ac.cbnu.computerengineering.patient.service;

import kr.ac.cbnu.computerengineering.admin.service.EtcServiceImpl;
import kr.ac.cbnu.computerengineering.common.datatype.*;
import kr.ac.cbnu.computerengineering.common.service.*;
import kr.ac.cbnu.computerengineering.common.util.Utils;
import kr.ac.cbnu.computerengineering.medicine.service.MedicineService;
import kr.ac.cbnu.computerengineering.prescription.service.RegistrationServiceImpl;
import kr.ac.cbnu.computerengineering.user.service.UserServiceImpl;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

public class PatientServiceImpl implements IPatientService {

	private IUserService userService;
	private IRegistrationService registrationService;
	private IMedicineService medicineService;
	
	public PatientServiceImpl() {
		this.userService = new UserServiceImpl();
		this.registrationService = new RegistrationServiceImpl();
		this.medicineService = new MedicineService();
	}

	/*
	어플리케이션 로그인을 처리하는 함수
	 */
	@Override
	public JSONObject getLoginUser(String ID, String password) throws Exception {
		UserDataType userDataType = Utils.makeUserDataType(ID, 
				password, null, null, null, null, null, null, 0, null);
		userDataType = this.userService.checkLogin(userDataType);
		JSONObject json = new JSONObject();
		if(userDataType.getRoles() == null) {
			json.put("resultType", "ERROR");
		}else {
			if(userDataType.getDisable().equals("Y")) {
				json.put("resultType", "NOT_APPROVAL");
			} else {
				json.put("user", userDataType);
				json.put("resultType", "LOGIN_SUCCESS");
			}
		}
		return json;
	}

	/*
	환자의 처방전을 갖고오는 함수
	 */
	@Override
	public List<PrescriptionDataType> getRegistrationAllData(String ID) throws Exception {
		List<RegistrationDataType> registrationDataTypeList = this.registrationService.selectPatientInfoRequest(ID);
		List<PrescriptionDataType> prescriptionDataTypeList = null;
		if(registrationDataTypeList != null){
			prescriptionDataTypeList = new ArrayList<PrescriptionDataType>();
			for(RegistrationDataType registration : registrationDataTypeList){
				prescriptionDataTypeList.addAll(registration.getPrescriptionList());
			}
		}
		return prescriptionDataTypeList;
	}


	/*
	검색 약제가 없을 시 환자에게 보여줄 내용을 만드는 함수
	 */
	private String makeNotExsistStr(List<String> notExist) {
		String notExistMessage = "";
		if(notExist.size() > 0) {
			notExistMessage += "처방된 약제 중 약제 코드 ";
			for(String code : notExist) {
				notExistMessage += code +", ";
			}
			notExistMessage += "del";
			notExistMessage = notExistMessage.replace(", del", "");
			notExistMessage += "는 데이터베이스에 없습니다.\n 검색하신 약제가 금기 약제 또는 주의 약제에 해당하는지 의사 선생님과 상의해주세요.\n 그 이외에 ";
			
		}
		return notExistMessage;
	}

	/*
	환자가 선택한 약제가 처방전으로 설정되어 있는지 판단하는 함수
	금기, 주의, 복용가능한 약물 리스트들의 상태에 따라 환자에게 보여지는 메세지가 다름
	 */
	private String compareMedicineDataTypeList(List<MedicineDataType> tolerables,
			List<MedicineDataType> uppers, List<MedicineDataType> prohibitions) {
		String message = "";
		if(prohibitions.size() > 0 && uppers.size() > 0 && tolerables.size() > 0) {
			message = this.generateResultMessage(PrescriptionResultType.TOLERABLE, prohibitions);
		} else if(prohibitions.size() > 0 && uppers.size() > 0 && tolerables.size() == 0) {
			message = this.generateResultMessage(PrescriptionResultType.PROHIBITION, prohibitions);
		} else if(prohibitions.size() > 0 && uppers.size() == 0 && tolerables.size() > 0) {
			message = this.generateResultMessage(PrescriptionResultType.TOLERABLE, prohibitions);
		} else if(prohibitions.size() > 0 && uppers.size() == 0 && tolerables.size() == 0) {
			message = this.generateResultMessage(PrescriptionResultType.PROHIBITION, prohibitions);
		} else if(prohibitions.size() == 0 && uppers.size() > 0 && tolerables.size() > 0) {
			message = this.generateResultMessage(PrescriptionResultType.TOLERABLE, tolerables);
		} else if(prohibitions.size() == 0 && uppers.size() > 0 && tolerables.size() == 0) {
			message = this.generateResultMessage(PrescriptionResultType.UPPER, uppers);
		} else if(prohibitions.size() == 0 && uppers.size() == 0 && tolerables.size() > 0) {
			message = this.generateResultMessage(PrescriptionResultType.TOLERABLE, tolerables);
		} else {
			message = this.generateResultMessage(PrescriptionResultType.TOLERABLE, tolerables);
		}
		return message;
	}

	/*
	compareMedicineDataTypeList에서 사용하는 메세지 작성 함수
	 */
	private String generateResultMessage(PrescriptionResultType prescriptionResult, List<MedicineDataType> medicines) {
		String result = "";
		
		switch (prescriptionResult) {
		case PROHIBITION:
			result += "처방된 약제 중 ";
			for(MedicineDataType medicine : medicines) {
				result+= medicine.getName()+", ";
			}
			result+= "del";
			result = result.replace(", del", "");
			result += "은(는) 금기 약제이므로 처방한 의사선생님께 해당 약제 복용 여부를 상의하세요.";
			break;
		case UPPER:
			result += "처방된 약제 중 ";
			for(MedicineDataType medicine : medicines) {
				result+= medicine.getName()+", ";
			}
			result += "del";
			result = result.replace(", del", "");
			result += "은(는) 주의 약제이므로 처방한 의사선생님께 해당 약제 복용 여부를 상의하세요.";
			break;
		default:
			result += "처방된 약제는 안전하게 복용할 수 있습니다.";
			break;
		}
		
		return result;
	}

	/*
	환자가 검색기능을 사용할 때 검색에 관련된 데이터를 만들어주는 함수
	 */
	@Override
	public PagingDataType makePrescriptionPage(int size, int nowPage) {
		PagingDataType pagingDataType = new PagingDataType();
		pagingDataType.setCount(size);
		pagingDataType.setEndPage(size);
		pagingDataType.setNowPage(nowPage);
		pagingDataType.setStartPage(1);
		pagingDataType.setPageCount(size);
		pagingDataType.setNowPageGroup(nowPage);
		return pagingDataType;
	}

	/*
	사용자가 선택한 약과 환자의 처방전을 비교하는 함수
	요청을 처리하는 함수이며 약물 내용은 MedicineSerivce의 함수를 사용
	약과 사용자의 처방전을 비교하는 함수는 checkPrescriptionWithMedine 사용
	 */
	@SuppressWarnings("unchecked")
	@Override
	public JSONObject checkMedicine(String searchOption, String searchValue,
			List<PrescriptionDataType> prescriptionList) throws Exception {
		SearchParam searchParam = new SearchParam();
		searchParam.setSearchOption("medicineCode");
		searchParam.setParam(searchValue);
		List<String> notExist = new ArrayList<String>();
		List<MedicineDataType> targetMedicines = this.medicineService.getMedicineNoLimitList(searchParam);
		if(this.IsMedicineListEmpty(targetMedicines)){
			notExist.add(searchValue);
		}
		String message = this.makeNotExsistStr(notExist) 
				+ this.checkPrescriptionWithMedicines(targetMedicines, prescriptionList, notExist);
		JSONObject json = new JSONObject();
		json.put("message", message);
		return json;
	}


	/*
	QR코드 로직을 처리하는 함수
	동작원리는 checkMedicine함수와 같음
	 */
	@SuppressWarnings("unchecked")
	@Override
	public JSONObject getQRCodeResult(List<PrescriptionDataType> prescriptionList, String[] medicineCodes) throws Exception {
		SearchParam searchParam = new SearchParam();
		searchParam.setSearchOption("medicineCode");
		List<String> notExist = new ArrayList<String>();
		List<MedicineDataType> targetMedicines = new ArrayList<MedicineDataType>();
		for(String medicineCode : medicineCodes){
			searchParam.setParam(medicineCode);
			List<MedicineDataType> oneMedicineList = this.medicineService.getMedicineNoLimitList(searchParam);
			if(this.IsMedicineListEmpty(oneMedicineList)){
				notExist.add(medicineCode);
				continue;
			}
			targetMedicines.addAll(oneMedicineList);
		}
		String message = this.makeNotExsistStr(notExist) 
				+ this.checkPrescriptionWithMedicines(targetMedicines, prescriptionList, notExist);
		JSONObject json = new JSONObject();
		json.put("message", message);
		return json;
		
	}

	/*
	사용자에게 처방된 금기, 약제, 복용가능에 대한 ATC코드 리스트를 생성
	검색한 약물과 사용자 처방 단계에 대한 비교는 compareMedicineATCWithPrescription 함수 사용
	 */
	private String checkPrescriptionWithMedicines(List<MedicineDataType> targetMedicines,
			List<PrescriptionDataType> prescriptionList, List<String> notExist) {
		List<MedicineDataType> tolerableMedicineList = null;
		List<MedicineDataType> upperMedicineList = null;
		List<MedicineDataType> prohibitionMedicineList = null;
		String message = "";
		List<PrescriptionATCDataType> tolerablePrescriptionATCDataTypeList = new ArrayList<>();
		List<PrescriptionATCDataType> upperPrescriptionATCDataTypeList = new ArrayList<>();
		List<PrescriptionATCDataType> prohibitionPrescriptionATCDataTypeList = new ArrayList<>();
		if(targetMedicines != null && targetMedicines.size() != 0){
			for(PrescriptionDataType prescription : prescriptionList) {
				tolerablePrescriptionATCDataTypeList.addAll(prescription.getTolerableList()); 
				upperPrescriptionATCDataTypeList.addAll(prescription.getUpperList());
				prohibitionPrescriptionATCDataTypeList.addAll(prescription.getProhibitionList());
			}
			tolerableMedicineList = this.compareMedicineATCWithPrescription(tolerablePrescriptionATCDataTypeList, targetMedicines);
			upperMedicineList = this.compareMedicineATCWithPrescription(upperPrescriptionATCDataTypeList, targetMedicines);
			prohibitionMedicineList = this.compareMedicineATCWithPrescription(prohibitionPrescriptionATCDataTypeList, targetMedicines);
			message = this.compareMedicineDataTypeList(tolerableMedicineList, upperMedicineList, prohibitionMedicineList);			
			
		}
		return message;
	}
	/*
	약물 중 사용자 처방단계에 설정된 ATC코드가 있는지 검사하는 함수
	 */
	private List<MedicineDataType> compareMedicineATCWithPrescription(
			List<PrescriptionATCDataType> prescriptionATCDataTypeList,
			List<MedicineDataType> targetMedicines) {
		List<MedicineDataType> result = new ArrayList<MedicineDataType>();
		for(MedicineDataType medicine : targetMedicines) {
			for(PrescriptionATCDataType atcDataType : prescriptionATCDataTypeList) {
				if(medicine.getATCCode() != null && Pattern.matches("^"+ atcDataType.getATCCode() + "(.)*", medicine.getATCCode().getCode())){
					result.add(medicine);
					break;
				} else {
					if(medicine.getIngredient() != null && medicine.getIngredient().getMaterials() != null) {
						for(MaterialDatatype material : medicine.getIngredient().getMaterials()) {
							if(material.getAtcs() != null) {
								for(AtcDataType atc : material.getAtcs()) {
									if(Pattern.matches("^"+ atcDataType.getATCCode() + "(.)*", atc.getCode())){
										result.add(medicine);
										break;
									}
								}
							}
						}
					}
				}
			}
		}
		
		return result;
	}

	private boolean IsMedicineListEmpty(List<MedicineDataType> oneMedicineList) {
		if(oneMedicineList == null){
			return true;
		}else if(oneMedicineList.size() == 0){
			return true;
		}
		return false;
	}
	/*
	처방전에 정의된 코멘트 내용을 보여주는 함수
	 */
	@Override
	public JSONObject getAllergyList(String userId) throws Exception {
		String allergy = "";
		List<PrescriptionDataType> prescriptionDataTypeList = this.getRegistrationAllData(userId);
		if(prescriptionDataTypeList.isEmpty()){
			allergy = String.valueOf(0);
		}else {
			for (PrescriptionDataType prescriptionDataType : prescriptionDataTypeList) {
				allergy += prescriptionDataType.getContentActionPlanDataType().getContent() + " ";
			}
		}
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("result", allergy);
		return jsonObject;
	}

	@SuppressWarnings("unchecked")
	/*
	이하 앱에서 가입, 가입내용수정, ID찾기, PW찾기 기능
	userService를 사용함
	 */


	@Override
	public JSONObject createUser(String ID, String password, String name, String disable,
			String email, String CBNUCode, String[] roles, int hospitalID, Date date) throws Exception {
		JSONObject json = new JSONObject();
		String approvalResult = this.userService.createUser(ID, password, name, disable, email, CBNUCode, roles, date, hospitalID); 
		json.put("resultType", approvalResult);
		return json;
	}

	@Override
	public void logByLoginCheck(String userId) throws Exception {
		this.userService.logByLoginCheck(new LogDataType(userId,null,Utils.getDate()));
	}

	@Override
	public void savePatientRequestLog(String id, int value, String[] codes) throws Exception {
		List<LogDataType> logs = new ArrayList<LogDataType>();
		for(String code : codes){
			logs.add(new LogDataType(id, code, Utils.getDate(),value));
		}
		this.userService.logQRCode(logs);
	}

	@Override
	public HospitalDatatype detailHospitalByUserID(String iD) throws Exception {
		return this.userService.detailUserByID(iD).getHospital();
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject getHospitalList() {
		List<HospitalDatatype> hospitalDatatypeList;
		JSONObject result = new JSONObject();
		JSONArray arrayResult = new JSONArray();
		try{
			IEctService adminService = new EtcServiceImpl();
			hospitalDatatypeList = adminService.getHospitals();
			result.put("result", this.changeFromHospitalToJSON(hospitalDatatypeList,arrayResult));
		}catch(Exception e){
			e.printStackTrace();
			result.put("result", "fail");
		}
		return result;
	}



	@SuppressWarnings("unchecked")
	private JSONArray changeFromHospitalToJSON(List<HospitalDatatype> hospitalDatatypeList, JSONArray arrayResult) {
		for(HospitalDatatype hospitalDatatype : hospitalDatatypeList){
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("name", hospitalDatatype.getName());
			jsonObject.put("id",hospitalDatatype.getId());
			arrayResult.add(jsonObject);
		}
		return arrayResult;
	}

}

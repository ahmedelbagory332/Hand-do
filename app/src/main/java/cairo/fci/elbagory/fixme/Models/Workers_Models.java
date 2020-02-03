package cairo.fci.elbagory.fixme.Models;

public class Workers_Models {

private String name;
    private String Section;
    private String phone;
    private String password;
    private long rate;
    private boolean IScheked;

    public boolean isIScheked() {
        return IScheked;
    }

    public void setIScheked(boolean IScheked) {
        this.IScheked = IScheked;
    }

    public long getRate() {
        return rate;
    }

    public void setRate(long rate) {
        this.rate = rate;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    private String img;
    public Workers_Models() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSection() {
        return Section;
    }

    public void setSection(String section) {
        Section = section;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


}

package bgu.spl.mics.application.objects;

public class MyCameras {
    private Camera[] CamerasConfigurations;
    private String camera_datas_path;

    @Override
    public String toString() {
        String str = "";
        for (Camera camera : CamerasConfigurations) {
            str +=" " +camera.toString() +"\n";
        }
        return str + camera_datas_path;
    }

    public String getCameraDatasPath() { return camera_datas_path;}

    public Camera[] getCamerasList() { return CamerasConfigurations;}
}

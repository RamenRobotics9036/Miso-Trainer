package simulationlib.shuffle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/**
 * Tests the MultiType class.
 */
public class MultiTypeTest {

  @Test
  public void testCreateBooleanType() {
    MultiType booleanType = MultiType.of(true);
    assertEquals("Boolean", booleanType.getType());
    assertEquals(Optional.of(true), booleanType.getBoolean());
  }

  @Test
  public void testCreateDoubleType() {
    MultiType doubleType = MultiType.of(10.5);
    assertEquals("Double", doubleType.getType());
    assertEquals(Optional.of(10.5), doubleType.getDouble());
  }

  @Test
  public void testCreateIntegerType() {
    MultiType integerType = MultiType.of(10);
    assertEquals("Integer", integerType.getType());
    assertEquals(Optional.of(10), integerType.getInteger());
  }

  @Test
  public void testCreateStringType() {
    MultiType stringType = MultiType.of("Hello");
    assertEquals("String", stringType.getType());
    assertEquals(Optional.of("Hello"), stringType.getString());
  }

  @Test
  public void testCreatePose2dType() {
    MultiType poseType = MultiType.of(new Pose2d(1.0, 2.0, new Rotation2d(4.0)));
    assertEquals("Pose2d", poseType.getType());
    assertEquals(Optional.of(new Pose2d(1.0, 2.0, new Rotation2d(4.0))), poseType.getPose2d());
  }

  @Test
  public void testNullBooleanCreation() {
    assertThrows(IllegalArgumentException.class, () -> MultiType.of((Boolean) null));
  }

  @Test
  public void testNullDoubleCreation() {
    assertThrows(IllegalArgumentException.class, () -> MultiType.of((Double) null));
  }

  @Test
  public void testNullIntegerCreation() {
    assertThrows(IllegalArgumentException.class, () -> MultiType.of((Integer) null));
  }

  @Test
  public void testNullStringCreation() {
    assertThrows(IllegalArgumentException.class, () -> MultiType.of((String) null));
  }

  @Test
  public void testNullPose2dCreation() {
    assertThrows(IllegalArgumentException.class, () -> MultiType.of((Pose2d) null));
  }

  @Test
  public void testGetWrongTypeBoolean() {
    MultiType stringType = MultiType.of("Test");
    assertEquals(Optional.empty(), stringType.getBoolean());
  }

  @Test
  public void testGetWrongTypeDouble() {
    MultiType booleanType = MultiType.of(false);
    assertEquals(Optional.empty(), booleanType.getDouble());
  }

  @Test
  public void testGetWrongTypeInteger() {
    MultiType stringType = MultiType.of("Test");
    assertEquals(Optional.empty(), stringType.getInteger());
  }

  @Test
  public void testGetWrongTypeString() {
    MultiType doubleType = MultiType.of(123.45);
    assertEquals(Optional.empty(), doubleType.getString());
  }

  @Test
  public void testGetWrongTypePose2d() {
    MultiType stringType = MultiType.of("Test");
    assertEquals(Optional.empty(), stringType.getPose2d());
  }

  @Test
  public void testSetBoolean() {
    MultiType booleanMultiType = MultiType.of(true);

    // Test setting a new boolean value
    booleanMultiType.setBoolean(false);
    assertTrue(false == booleanMultiType.getBoolean().orElseThrow());

    // Test setting null (should throw IllegalArgumentException)
    assertThrows(IllegalArgumentException.class, () -> booleanMultiType.setBoolean(null));

    // Test setting boolean on a double type (should throw IllegalStateException)
    MultiType doubleMultiType = MultiType.of(1.0);
    assertThrows(IllegalStateException.class, () -> doubleMultiType.setBoolean(true));
  }

  @Test
  public void testSetDouble() {
    MultiType doubleMultiType = MultiType.of(1.0);

    // Test setting a new double value
    doubleMultiType.setDouble(2.0);
    assertEquals(2.0, doubleMultiType.getDouble().orElseThrow());

    // Test setting null (should throw IllegalArgumentException)
    assertThrows(IllegalArgumentException.class, () -> doubleMultiType.setDouble(null));

    // Test setting double on a string type (should throw IllegalStateException)
    MultiType stringMultiType = MultiType.of("test");
    assertThrows(IllegalStateException.class, () -> stringMultiType.setDouble(3.0));
  }

  @Test
  public void testSetInteger() {
    MultiType integerMultiType = MultiType.of(1);

    // Test setting a new integer value
    integerMultiType.setInteger(2);
    assertEquals(2, integerMultiType.getInteger().orElseThrow());

    // Test setting null (should throw IllegalArgumentException)
    assertThrows(IllegalArgumentException.class, () -> integerMultiType.setInteger(null));

    // Test setting integer on a string type (should throw IllegalStateException)
    MultiType stringMultiType = MultiType.of("test");
    assertThrows(IllegalStateException.class, () -> stringMultiType.setInteger(3));
  }

  @Test
  public void testSetString() {
    MultiType stringMultiType = MultiType.of("test");

    // Test setting a new string value
    stringMultiType.setString("new test");
    assertEquals("new test", stringMultiType.getString().orElseThrow());

    // Test setting null (should throw IllegalArgumentException)
    assertThrows(IllegalArgumentException.class, () -> stringMultiType.setString(null));

    // Test setting string on a boolean type (should throw IllegalStateException)
    MultiType booleanMultiType = MultiType.of(true);
    assertThrows(IllegalStateException.class, () -> booleanMultiType.setString("false"));
  }

  @Test
  public void testSetPose2d() {
    MultiType pose2dMultiType = MultiType.of(new Pose2d(1.0, 2.0, new Rotation2d(4.0)));

    // Test setting a new pose2d value
    pose2dMultiType.setPose2d(new Pose2d(2.0, 3.0, new Rotation2d(5.0)));
    assertEquals(new Pose2d(2.0, 3.0, new Rotation2d(5.0)),
        pose2dMultiType.getPose2d().orElseThrow());

    // Test setting null (should throw IllegalArgumentException)
    assertThrows(IllegalArgumentException.class, () -> pose2dMultiType.setPose2d(null));

    // Test setting pose2d on a boolean type (should throw IllegalStateException)
    MultiType booleanMultiType = MultiType.of(true);
    assertThrows(IllegalStateException.class, () -> booleanMultiType.setPose2d(new Pose2d()));
  }

  // Unit tests for CopyTo method
  @Test
  public void testCopyToBoolean() {
    MultiType booleanMultiType = MultiType.of(true);
    MultiType otherBooleanMultiType = MultiType.of(false);

    booleanMultiType.copyTo(otherBooleanMultiType);
    assertEquals(Optional.of(true), otherBooleanMultiType.getBoolean());
  }

  @Test
  public void testCopyToDouble() {
    MultiType doubleMultiType = MultiType.of(1.0);
    MultiType otherDoubleMultiType = MultiType.of(2.0);

    doubleMultiType.copyTo(otherDoubleMultiType);
    assertEquals(Optional.of(1.0), otherDoubleMultiType.getDouble());
  }

  @Test
  public void testCopyToInteger() {
    MultiType integerMultiType = MultiType.of(1);
    MultiType otherIntegerMultiType = MultiType.of(2);

    integerMultiType.copyTo(otherIntegerMultiType);
    assertEquals(Optional.of(1), otherIntegerMultiType.getInteger());
  }

  @Test
  public void testCopyToString() {
    MultiType stringMultiType = MultiType.of("test");
    MultiType otherStringMultiType = MultiType.of("other test");

    stringMultiType.copyTo(otherStringMultiType);
    assertEquals(Optional.of("test"), otherStringMultiType.getString());
  }

  @Test
  public void testCopyToPose2d() {
    MultiType pose2dMultiType = MultiType.of(new Pose2d(1.0, 2.0, new Rotation2d(4.0)));
    MultiType otherPose2dMultiType = MultiType.of(new Pose2d(2.0, 3.0, new Rotation2d(5.0)));

    pose2dMultiType.copyTo(otherPose2dMultiType);
    assertEquals(Optional.of(new Pose2d(1.0, 2.0, new Rotation2d(4.0))),
        otherPose2dMultiType.getPose2d());
  }

  @Test
  public void testCopyToDifferentTypeBool() {
    MultiType stringMultiType = MultiType.of("test");
    MultiType booleanMultiType = MultiType.of(true);

    assertThrows(IllegalStateException.class, () -> stringMultiType.copyTo(booleanMultiType));
  }

  @Test
  public void testCopyToDifferentTypeDouble() {
    MultiType stringMultiType = MultiType.of("test");
    MultiType doubleMultiType = MultiType.of(1.0);

    assertThrows(IllegalStateException.class, () -> stringMultiType.copyTo(doubleMultiType));
  }

  @Test
  public void testCopyToDifferentTypeInteger() {
    MultiType stringMultiType = MultiType.of("test");
    MultiType integerMultiType = MultiType.of(1);

    assertThrows(IllegalStateException.class, () -> stringMultiType.copyTo(integerMultiType));
  }

  @Test
  public void testCopyToDifferentTypeString() {
    MultiType booleanMultiType = MultiType.of(true);
    MultiType stringMultiType = MultiType.of("test");

    assertThrows(IllegalStateException.class, () -> booleanMultiType.copyTo(stringMultiType));
  }

  @Test
  void testCopyToDifferentTypePose2d() {
    MultiType stringMultiType = MultiType.of("test");
    MultiType pose2dMultiType = MultiType.of(new Pose2d(1.0, 2.0, new Rotation2d(4.0)));

    assertThrows(IllegalStateException.class, () -> stringMultiType.copyTo(pose2dMultiType));
  }

  @Test
  public void testCopyToNull() {
    MultiType booleanMultiType = MultiType.of(true);
    MultiType nullMultiType = null;

    assertThrows(IllegalArgumentException.class, () -> booleanMultiType.copyTo(nullMultiType));
  }
}

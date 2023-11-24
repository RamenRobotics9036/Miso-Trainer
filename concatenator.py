import os
import argparse

# Function to recursively search for .java files in a directory
def find_java_files(directory):
    java_files = []
    for root, _, files in os.walk(directory):
        for file in files:
            if file.endswith(".java"):
                java_files.append(os.path.join(root, file))
    return java_files

# Function to concatenate java files into a new file with formatting
def concatenate_java_files(java_files, output_file):
    with open(output_file, 'w') as outfile:
        for java_file in java_files:
            filename = os.path.basename(java_file)
            outfile.write("=" * 65 + '\n')
            outfile.write(f"FileName: {java_file}\n")
            outfile.write("=" * 65 + '\n')
            with open(java_file, 'r') as infile:
                outfile.write(infile.read() + '\n')

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Concatenate .java files in a directory")
    parser.add_argument("directory", help="Directory path to search for .java files")
    parser.add_argument("output_file", help="Output file name for concatenated content")

    args = parser.parse_args()

    directory_to_search = args.directory
    output_file = args.output_file

    java_files = find_java_files(directory_to_search)

    if java_files:
        concatenate_java_files(java_files, output_file)
        print(f"Concatenated {len(java_files)} .java files into {output_file}")
    else:
        print("No .java files found in the specified directory.")
